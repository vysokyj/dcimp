package cz.jerzy.dcimp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpReader;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        return mediaFile;
    }

    private static void loadMetadata(MediaFile mediaFile) {
        try {
            mediaFile.setMetadata(ImageMetadataReader.readMetadata(mediaFile.getPath().toFile()));
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }

    private static void loadSidecars(MediaFile mediaFile) {
        try {
            mediaFile.setSidecars(FileSystem.getSidecarFiles(mediaFile.getPath()).stream()
                    .filter(sidecarFile -> isSupportedSidecarFile(mediaFile, sidecarFile))
                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }

    private static void loadXmpMetadata(MediaFile mediaFile) {
        try {
            mediaFile.setXmpMetadata(mediaFile.getOptionalXmpSidecarPath()
                    .map(FileSystem::readXmpMetadata)
                    .orElse(null));
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }

    private static Metadata readXmpMetadata(Path path) {
        try {
            Metadata xmpMetadata = new Metadata();
            XmpReader xmpReader = new XmpReader();
            xmpReader.extract(Files.readAllBytes(path), xmpMetadata);
            return xmpMetadata;
        } catch (Exception e) {
            throw new ProcessException(e);
        }
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
        if (mediaFile.getType().isRawImage() && ext.equals(".JPG")) return true;
        else return mediaFile.getType().isImage() && ext.equals(".XMP");
    }

    public static void copy(Path source, Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.copy(source, target);

        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }
}
