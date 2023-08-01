package cz.jerzy.dcimp;


import static cz.jerzy.dcimp.FileSystem.loadMediaFile;
import static java.nio.file.Files.isRegularFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootApplication
public class Application implements ApplicationRunner {

    private final SimpleDateFormat sddf = new SimpleDateFormat("yyyy/MM/dd");
    private final SimpleDateFormat fddf = new SimpleDateFormat("HHmmss");

    private Path outputDirectory;
    private Path[] inputDirectories;

    private static final Application instance = new Application();

    public static Application getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileSystem.requireDirectory(outputDirectory);
        Arrays.stream(inputDirectories)
                .peek(FileSystem::requireDirectory)
                .forEach(this::importDirectory);
    }

    private void importDirectory(Path path) {
        log.debug("Importing directory {}", path);
        try (Stream<Path> walk = Files.walk(path)) {
            walk
                    .filter(Files::isRegularFile)
                    .filter(FileSystem::isSupportedFile)
                    .forEach(this::importFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void importFile(Path source) {
        log.debug("Importing file {}", source);
        MediaFile mediaFile = FileSystem.loadMediaFile(source);
        if (FileSystem.isSidecar(source, mediaFile.getSidecars())) {
            log.debug("File {} - is sidecar of some raw file - skipping", source);
            return;
        }
        if (isAlreadyImported(mediaFile)) {
            log.warn("File {} - already imported - skipping", source);
            return;
        }
        findOutputPath(mediaFile).ifPresent(output -> FileSystem.copy(source, output));
    }

    private boolean isAlreadyImported(MediaFile mediaFile) {
        for (int i = 0; i < 100; i++) {
            Path path = createOutputPath(mediaFile, i);
            if (isRegularFile(path)) {
                MediaFile storedFile = loadMediaFile(path);
                return Objects.equals(
                        storedFile.getSfvMetadata().getChecksum(),
                        mediaFile.getSfvMetadata().getChecksum());
            }
        }
        return false;
    }

    private Optional<Path> findOutputPath(MediaFile mediaFile) {
        if (mediaFile.getCrateDate() == null) return Optional.empty();
        for (int i = 0; i < 100; i++) {
            Path path = createOutputPath(mediaFile, i);
            if (!path.toFile().exists()) return Optional.of(path);
        }
        log.warn("Unable to find free output path slot for file {}", mediaFile);
        return Optional.empty();
    }

    private Path createOutputPath(MediaFile imported, int i) {
        String root = outputDirectory.toString();
        String directory = sddf.format(imported.getCrateDate());
        String prefix = fddf.format(imported.getCrateDate());
        String extension = FileSystem.getUnixExtensionOf(imported.getPath()).orElse("");
        String filename = String.format("%s%02d%s", prefix, i, extension);
        return Path.of(root, directory, filename).normalize().toAbsolutePath();
    }
}
