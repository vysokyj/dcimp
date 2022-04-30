package cz.jerzy.dcimp;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpWriter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPUtility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaFile {

    private Path path;
    private MediaType type;
    private Set<Path> sidecars;
    private Metadata metadata;
    private Metadata xmpMetadata;
    private SfvMetadata sfvMetadata;
    private String checksum;

    private final XMPUtility xmpUtility = new XMPUtility();

    public Date getCrateDate() {
        return Optional.ofNullable(metadata)
                .map(m -> m.getDirectoriesOfType(ExifSubIFDDirectory.class))
                .flatMap(d -> d.stream()
                        .filter(exif -> exif.getDateOriginal() != null)
                        .findFirst())
                .map(ExifSubIFDDirectory::getDateOriginal)
                .orElse(null);
    }

    public void printMetadata() {
        if (metadata == null) return;
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.format("[%s] - %s = %s\n",
                        directory.getName(), tag.getTagName(), tag.getDescription());
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s\n", error);
                }
            }
        }
    }

    public void createXmpMetadata() {
        final Set<XMPTag> tags = Set.of(
                XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("OriginalFileName")
                        .value(path.getFileName().toString())
                        .build()
        );
        xmpMetadata = xmpUtility.convert(metadata, tags);
    }

    public void printXmpMetadata() {
        try (PrintStream outputStream = new PrintStream(System.out, true, StandardCharsets.UTF_8)) {
            XmpWriter.write(outputStream, xmpMetadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getXmpMetadataAsString() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XmpWriter.write(outputStream, xmpMetadata);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Path> getOptionalXmpSidecarPath() {
        return sidecars.stream()
                .filter(p -> p.getFileName().toString().toUpperCase().endsWith(".XMP"))
                .findFirst();
    }

    public Optional<Path> getOptionalSfvSidecarPath() {
        return sidecars.stream()
                .filter(p -> p.getFileName().toString().toUpperCase().endsWith(".SFV"))
                .findFirst();
    }

}
