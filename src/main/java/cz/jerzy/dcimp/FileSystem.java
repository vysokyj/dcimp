package cz.jerzy.dcimp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpReader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import static java.lang.Long.toHexString;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.writeString;
import static org.apache.commons.io.FileUtils.checksum;
import static org.apache.commons.lang3.StringUtils.*;

@UtilityClass
public class FileSystem {

    public static Optional<String> getExtensionOf(Path path) {
        return Optional.ofNullable(path.getFileName())
                .map(Path::toString)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")));
    }

    public static Optional<String> getDosExtensionOf(Path path) {
        return getExtensionOf(path)
                .map(FileSystem::toDosExtension);
    }

    public static Optional<String> getUnixExtensionOf(Path path) {
        return getExtensionOf(path)
                .map(FileSystem::toUnixExtension);
    }

    public static Set<Path> getSidecarFiles(Path path) {
        if (!Files.isRegularFile(path)) {
            return Collections.emptySet();
        }
        try (Stream<Path> list = Files.list(path.getParent())) {
            return list
                    .filter(tested -> !tested.equals(path))
                    .filter(tested -> isSidecarFile(path, tested))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public static Path requireDirectory(Path path) {
        if (!Files.isDirectory(path))
            throw new IllegalArgumentException("Required directory");
        return path;
    }

    public static Path requireRegularFile(Path path) {
        if (!Files.isRegularFile(path))
            throw new IllegalArgumentException("Required file");
        return path;
    }

    public static String removeExtension(String filename) {
        int i = filename.lastIndexOf('.');
        if (i > 0) return filename.substring(0, i);
        else return filename;
    }

    public static boolean isSidecarFile(Path p1, Path p2) {
        if (!p1.getParent().equals(p2.getParent())) return false;
        String n1 = p1.getFileName().toString().toLowerCase();
        String n2 = p2.getFileName().toString().toLowerCase();
        return removeExtension(n1).equals(removeExtension(n2)) ||
                n1.equals(removeExtension(n2)) || n2.equals(removeExtension(n1));
    }

    public static String toDosExtension(String extension) {
        if (extension.length() <= 4) return extension.toUpperCase();
        String ext = extension.toUpperCase();
        switch (ext) {
            case ".JPEG":
                return ".JPG";
            case ".MPEG":
                return ".MPG";
            default:
                return ext.substring(0, 4);
        }
    }

    public static String toUnixExtension(String extension) {
        String ext = extension.toLowerCase();
        switch (ext) {
            case ".jpg":
                return ".jpeg";
            case ".mpg":
                return ".mpeg";
            default:
                return ext;
        }
    }

    public static boolean isSupportedFile(Path path) {
        return getMediaType(path) != null;
    }

    public static MediaFile loadMediaFile(Path path) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setType(FileSystem.getMediaType(path));
        mediaFile.setPath(path);
        loadMetadata(mediaFile);
        loadSidecars(mediaFile);
        loadXmpMetadata(mediaFile);
        loadSvfMetadata(mediaFile);
        return mediaFile;
    }

    @SneakyThrows
    private static void loadMetadata(MediaFile mediaFile) {
        mediaFile.setMetadata(ImageMetadataReader.readMetadata(mediaFile.getPath().toFile()));
    }

    @SneakyThrows
    private static void loadSidecars(MediaFile mediaFile) {
        mediaFile.setSidecars(FileSystem.getSidecarFiles(mediaFile.getPath()).stream()
                .filter(sidecarFile -> isSupportedSidecarFile(mediaFile, sidecarFile))
                .collect(Collectors.toSet()));
    }

    @SneakyThrows
    private static void loadXmpMetadata(MediaFile mediaFile) {
        mediaFile.setXmpMetadata(mediaFile.getOptionalXmpSidecarPath()
                .map(FileSystem::readXmpMetadata)
                .orElse(null));
    }

    @SneakyThrows
    private static Metadata readXmpMetadata(Path path) {
        Metadata xmpMetadata = new Metadata();
        XmpReader xmpReader = new XmpReader();
        xmpReader.extract(Files.readAllBytes(path), xmpMetadata);
        return xmpMetadata;
    }

    @SneakyThrows
    private static void loadSvfMetadata(MediaFile mediaFile) {
        mediaFile.setSfvMetadata(mediaFile.getOptionalSfvSidecarPath()
                .map(FileSystem::loadSvfMetadata)
                .orElseGet(() -> createSfvMetadata(mediaFile)));
    }

    @SneakyThrows
    public static SfvMetadata createSfvMetadata(MediaFile mediaFile) {
        String filename = mediaFile.getPath().getFileName().toString();
        String checksum = calculateChecksum(mediaFile.getPath());
        SfvMetadata metadata = new SfvMetadata();
        metadata.setImported(new Date());
        metadata.setCreated(mediaFile.getCrateDate());
        metadata.setOriginal(filename);
        metadata.setChecksums(Collections.singletonList(Pair.of(filename, checksum)));
        return metadata;
    }

    @SneakyThrows
    public static SfvMetadata loadSvfMetadata(Path path) {
       return new SfvMetadata(readAllLines(path));
    }

    @SneakyThrows
    public static void saveSfvMetadata(Path path, SfvMetadata metadata) {
        writeString(path, metadata.toString());
    }

    public static boolean isSidecar(Path source, Set<Path> sidecars) {
        if (MediaType.JPG.equals(FileSystem.getMediaType(source))) {
            return sidecars.stream()
                    .map(FileSystem::getMediaType)
                    .anyMatch(MediaType::isRawImage);
        }
        return false;
    }

    public static MediaType getMediaType(Path path) {
        return FileSystem.getDosExtensionOf(path)
                .map(ext -> ext.substring(1))
                .flatMap(ext -> Arrays.stream(MediaType.values())
                        .filter(type -> type.toString().equals(ext))
                        .findFirst())
                .orElseThrow(IllegalArgumentException::new);
    }

    private static boolean isSupportedSidecarFile(MediaFile mediaFile, Path sidecarFile) {
        String ext = FileSystem.getDosExtensionOf(sidecarFile).orElseThrow(IllegalArgumentException::new);
        if (ext.equals(".SFV")) return true;
        if (mediaFile.getType().isRawImage() && ext.equals(".JPG")) return true;
        else return mediaFile.getType().isImage() && ext.equals(".XMP");
    }

    @SneakyThrows
    public static void copy(Path source, Path target) {
        Files.createDirectories(target.getParent());
        Files.copy(source, target);
    }

    @SneakyThrows
    public static String calculateChecksum(Path path) {
        return upperCase(toHexString(checksum(path.toFile(), new CRC32()).getValue()));
    }

}
