package cz.jerzy.dcimp;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class FileSystemTest {

    @Test
    void getExtensionOf() {
        Path path = Path.of("TEST", "IMG_001.ARW");

        String result = FileSystem.getExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".ARW");
    }

    @Test
    void getDosExtensionOfMpeg() {
        Path path = Path.of("TEST", "001.mpeg");

        String result = FileSystem.getDosExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".MPG");
    }

    @Test
    void getDosExtensionOfJpeg() {
        Path path = Path.of("TEST", "001.jpeg");

        String result = FileSystem.getDosExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".JPG");
    }

    @Test
    void getDosExtensionOfArw() {
        Path path = Path.of("TEST", "001.arw");

        String result = FileSystem.getDosExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".ARW");
    }

    @Test
    void getUnixExtensionOfMpeg() {
        Path path = Path.of("TEST", "001.MPG");

        String result = FileSystem.getUnixExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".mpeg");
    }

    @Test
    void getUnixExtensionOfJpeg() {
        Path path = Path.of("TEST", "001.JPG");

        String result = FileSystem.getUnixExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".jpeg");
    }

    @Test
    void getUnixExtensionOfArw() {
        Path path = Path.of("TEST", "001.ARW");

        String result = FileSystem.getUnixExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".arw");
    }

    @Test
    void removeExtension() {
        String result = FileSystem.removeExtension("test.jpg");

        assertThat(result).isEqualTo("test");
    }

    @Test
    void isSidecarFile() {
        Path p1 = Path.of("TEST", "IMG_001.ARW");
        Path p2 = Path.of("TEST", "IMG_001.xmp");
        Path p3 = Path.of("TEST", "IMG_001.ARW.xmp");

        assertThat(FileSystem.isSidecarFile(p1, p2)).isTrue();
        assertThat(FileSystem.isSidecarFile(p1, p3)).isTrue();
    }

    @Test
    void loadMediaFile_givenSonyRaw() throws URISyntaxException {
        URL url = getClass().getResource("/samples/DSC00053.ARW");
        assertThat(url).isNotNull();
        Path path = Paths.get(url.toURI());
        MediaFile mediaFile = FileSystem.loadMediaFile(path);
        mediaFile.printMetadata();
        mediaFile.printXmpMetadata();
        mediaFile.createXmpMetadata();
        mediaFile.printXmpMetadata();
        assertThat(mediaFile).isNotNull();
        assertThat(mediaFile.getCrateDate()).isNotNull();
        assertThat(mediaFile.getSidecars()).hasSize(3);
        assertThat(mediaFile.getType().isImage()).isTrue();
        assertThat(mediaFile.getOptionalSfvSidecarPath()).isNotEmpty();
        assertThat(mediaFile.getSfvMetadata()).isNotNull();
        assertThat(mediaFile.getSfvMetadata().getChecksums()).hasSize(1);
        assertThat(mediaFile.getSfvMetadata().getCreated()).isEqualTo("2015-08-10T21:00:00.090Z");
        assertThat(mediaFile.getSfvMetadata().getImported()).isEqualTo("2015-08-10T21:00:00.090Z");
        assertThat(mediaFile.getSfvMetadata().getOriginal()).isEqualTo("DSC00053.ARW");
        assertThat(mediaFile.getSfvMetadata().getFilename()).isEqualTo("DSC00053.ARW");
        assertThat(mediaFile.getSfvMetadata().getChecksum()).isEqualTo("374ABBA9");
        assertThat(mediaFile.getSfvMetadata().getChecksums().get(0).getKey()).isEqualTo("DSC00053.ARW");
        assertThat(mediaFile.getSfvMetadata().getChecksums().get(0).getValue()).isEqualTo("374ABBA9");
    }

    @Test
    void loadMediaFile_givenNikonRaw() {
        final Path path = loadResourceAsPath("/samples/DSC_0382.NEF");

        MediaFile mediaFile = FileSystem.loadMediaFile(path);
        mediaFile.printMetadata();

        assertThat(mediaFile).isNotNull();
        assertThat(mediaFile.getCrateDate()).isNotNull();
        assertThat(mediaFile.getSidecars()).hasSize(1);
        assertThat(mediaFile.getType().isImage()).isTrue();
    }

    @Test
    void loadSvfMetadata() {
        final Path path = loadResourceAsPath("/samples/DSC00053.sfv");

        SfvMetadata metadata = FileSystem.loadSvfMetadata(path);

        assertThat(metadata).isNotNull();
        assertThat(metadata.getChecksums()).hasSize(1);
        assertThat(metadata.getCreated()).isEqualTo("2015-08-10T21:00:00.090Z");
        assertThat(metadata.getImported()).isEqualTo("2015-08-10T21:00:00.090Z");
        assertThat(metadata.getOriginal()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getFilename()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getChecksum()).isEqualTo("374ABBA9");
        assertThat(metadata.getChecksums().get(0).getKey()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getChecksums().get(0).getValue()).isEqualTo("374ABBA9");
    }

    @Test
    void createSfvMetadata() {
        final Path path = loadResourceAsPath("/samples/DSC_0382.NEF");
        MediaFile mediaFile = new MediaFile();
        mediaFile.setPath(path);

        SfvMetadata metadata = FileSystem.createSfvMetadata(mediaFile);

        assertThat(metadata).isNotNull();
        assertThat(metadata.getChecksums()).hasSize(1);
        assertThat(metadata.getCreated()).isNull();
        assertThat(metadata.getImported()).isNotNull();
        assertThat(metadata.getOriginal()).isEqualTo("DSC_0382.NEF");
        assertThat(metadata.getFilename()).isEqualTo("DSC_0382.NEF");
        assertThat(metadata.getChecksum()).isEqualTo("7DF8447D");
        assertThat(metadata.getChecksums().get(0).getKey()).isEqualTo("DSC_0382.NEF");
        assertThat(metadata.getChecksums().get(0).getValue()).isEqualTo("7DF8447D");
    }

    @SneakyThrows
    @Test
    void saveSfvMetadata() {
        final Path inputPath = loadResourceAsPath("/samples/DSC00053.sfv");
        final Path outputPath = Files.createTempFile("dsc", ".sfv");
        SfvMetadata inputMetadata = FileSystem.loadSvfMetadata(inputPath);

        FileSystem.saveSfvMetadata(outputPath, inputMetadata);

        SfvMetadata outputMetadata = FileSystem.loadSvfMetadata(outputPath);

        assertThat(outputMetadata).isNotNull();
        assertThat(inputMetadata).isEqualTo(outputMetadata);
        assertThat(inputMetadata.toString()).isEqualTo(outputMetadata.toString());
    }

    @SneakyThrows
    @Test
    void copy() {
        final Path source = loadResourceAsPath("/samples/DSC00053.sfv");
        final Path target = Path.of(Files.createTempDirectory("copy").toString(), "DSC00053.sfv");

        FileSystem.copy(source, target);

        assertThat(source).isRegularFile();
        assertThat(target).isRegularFile();
    }

    @SneakyThrows
    private Path loadResourceAsPath(String resource) {
        URL url = getClass().getResource(resource);
        assertThat(url).isNotNull();
        return Paths.get(url.toURI());
    }
}