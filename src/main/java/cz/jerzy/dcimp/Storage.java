package cz.jerzy.dcimp;

import cz.jerzy.dcimp.jpa.JpaEntityManagerFactory;
import cz.jerzy.dcimp.jpa.model.Image;
import cz.jerzy.dcimp.jpa.model.Stream;
import cz.jerzy.dcimp.jpa.repository.ImageRepository;
import cz.jerzy.dcimp.jpa.repository.ImageRepositoryImpl;
import lombok.Data;

import javax.persistence.EntityManager;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class Storage {

    private Path rootDirectory;
    private ImageRepository imageRepository;

    public Storage(Path rootDirectory) {
        if (!Files.isDirectory(rootDirectory)) {
            throw new RuntimeException("Required directory");
        }
        this.rootDirectory = rootDirectory.toAbsolutePath();
        initDatabase(Path.of(rootDirectory.toAbsolutePath().toString(), "dcimp"));
    }

    public Storage(String rootDirectory) {
        this(Path.of(rootDirectory));
    }

    private void initDatabase(Path file) {
        JpaEntityManagerFactory jpaFactory =  new JpaEntityManagerFactory(
                file,
                new Class[]{
                        Image.class,
                        Stream.class
                });
        EntityManager em = jpaFactory.getEntityManager();
        imageRepository = new ImageRepositoryImpl(em);
    }

    public void add(MediaFile mediaFile) {
        Image image = new Image();
        image.setChecksum(mediaFile.getChecksum());
        image.setOriginalFileName(mediaFile.getPath().getFileName());
        imageRepository.saveImage(image);
    }

    public void checkIntegrity() {

    }

}
