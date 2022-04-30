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
    void getDosExtensionOf() {
        Path path = Path.of("TEST", "001.mpeg");

        String result = FileSystem.getDosExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".MPG");
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
        assertThat(mediaFile.getSfvMetadata().getCreated()).isEqualTo("2015-08-10T23:00:00.090");
        assertThat(mediaFile.getSfvMetadata().getImported()).isEqualTo("2015-08-10T23:00:00.090");
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
        assertThat(mediaFile.getSidecars()).hasSize(2);
        assertThat(mediaFile.getType().isImage()).isTrue();
    }

    @Test
    void loadSvfMetadata() {
        final Path path = loadResourceAsPath("/samples/DSC00053.sfv");

        SfvMetadata metadata = FileSystem.loadSvfMetadata(path);

        assertThat(metadata).isNotNull();
        assertThat(metadata.getChecksums()).hasSize(1);
        assertThat(metadata.getCreated()).isEqualTo("2015-08-10T23:00:00.090");
        assertThat(metadata.getImported()).isEqualTo("2015-08-10T23:00:00.090");
        assertThat(metadata.getOriginal()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getFilename()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getChecksum()).isEqualTo("374ABBA9");
        assertThat(metadata.getChecksums().get(0).getKey()).isEqualTo("DSC00053.ARW");
        assertThat(metadata.getChecksums().get(0).getValue()).isEqualTo("374ABBA9");
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
    private Path loadResourceAsPath(String resource) {
        URL url = getClass().getResource(resource);
        assertThat(url).isNotNull();
        return Paths.get(url.toURI());
    }
}