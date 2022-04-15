package cz.jerzy.dcimp.jpa.repository;

import cz.jerzy.dcimp.jpa.model.Image;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class ImageRepositoryImpl implements ImageRepository{

    private EntityManager em;

    public ImageRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Image getImageById(Long id) {
        return em.find(Image.class, id);
    }

    @Override
    public Image getImageByChecksum(String checksum) {
        TypedQuery<Image> q = em.createQuery("SELECT i FROM Image i WHERE i.checksum = :checksum", Image.class);
        q.setParameter("checksum", checksum);
        return q.getSingleResult();
    }

    @Override
    public Image saveImage(Image image) {
        if (image.getId() == null) {
            em.persist(image);
        } else {
            image = em.merge(image);
        }
        return image;
    }

    @Override
    public void deleteImage(Image image) {
        if (em.contains(image)) {
            em.remove(image);
        } else {
            em.merge(image);
        }
    }
}
