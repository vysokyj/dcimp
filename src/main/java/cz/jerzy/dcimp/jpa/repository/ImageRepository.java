package cz.jerzy.dcimp.jpa.repository;

import cz.jerzy.dcimp.jpa.model.Image;

public interface ImageRepository {

    Image getImageById(Long id);

    Image getImageByChecksum(String hash);

    Image saveImage(Image b);

    void deleteImage(Image b);

}
