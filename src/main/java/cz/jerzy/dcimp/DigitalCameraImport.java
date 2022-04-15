package cz.jerzy.dcimp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;


@Command(name = "dcimp", mixinStandardHelpOptions = true, version = "dcimp 0.1",
        description = "Imports digital camera images to given directory.")
public class DigitalCameraImport implements Callable<Integer> {

    private static final SimpleDateFormat SDDF = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat FDDF = new SimpleDateFormat("HHmmss");

    private final Logger log = LoggerFactory.getLogger(DigitalCameraImport.class);

    @Option(names = {"-o", "--out"}, description = "output directory")
    private Path outputDirectory;

    @Parameters(paramLabel = "DIRECTORY", description = "one ore more directories to import")
    private Path[] inputDirectories;

    private Storage storage;

    private static final DigitalCameraImport instance = new DigitalCameraImport();

    public static DigitalCameraImport getInstance() {
        return instance;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new DigitalCameraImport()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        storage = new Storage(outputDirectory);
        FileSystem.requireDirectory(outputDirectory);
        Arrays.stream(inputDirectories)
                .peek(FileSystem::requireDirectory)
                .forEach(this::importDirectory);
        return 0;
    }

    private void importDirectory(Path path) {
        log.debug("Importing directory {}", path);
        try {
            Files.walk(path)
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
            if (Files.isRegularFile(path) && MD5.verifyChecksum(path, MD5.calculateChecksum(mediaFile.getPath())))
                return true;
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
        String directory = SDDF.format(imported.getCrateDate());
        String prefix = FDDF.format(imported.getCrateDate());
        String extension = FileSystem.getUnixExtensionOf(imported.getPath()).orElse("");
        String filename = String.format("%s%02d%s", prefix, i, extension);
        return Path.of(root, directory, filename).normalize().toAbsolutePath();
    }

}
