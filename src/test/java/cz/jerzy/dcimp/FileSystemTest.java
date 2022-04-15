package cz.jerzy.dcimp;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class FileSystemTest {

    @Test
    void getExtensionOf() {
        Path path = Path.of("C:\\TEST\\IMG_001.ARW");

        String result = FileSystem.getExtensionOf(path).orElse(null);

        assertThat(result).isEqualTo(".ARW");
    }

    @Test
    void getDosExtensionOf() {
        Path path = Path.of("C:\\TEST\\001.mpeg");

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
        Path p1 = Path.of("C:\\TEST\\IMG_001.ARW");
        Path p2 = Path.of("C:\\TEST\\IMG_001.xmp");
        Path p3 = Path.of("C:\\TEST\\IMG_001.ARW.xmp");

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
        assertThat(mediaFile.getSidecars()).hasSize(2);
        assertThat(mediaFile.getType().isImage()).isTrue();
    }

    @Test
    void loadMediaFile_givenNikonRaw() throws URISyntaxException {
        URL url = getClass().getResource("/samples/DSC_0382.NEF");
        assertThat(url).isNotNull();
        Path path = Paths.get(url.toURI());
        MediaFile mediaFile = FileSystem.loadMediaFile(path);
        mediaFile.printMetadata();

        assertThat(mediaFile).isNotNull();
        assertThat(mediaFile.getCrateDate()).isNotNull();
        assertThat(mediaFile.getSidecars()).isEmpty();
        assertThat(mediaFile.getType().isImage()).isTrue();
    }
}