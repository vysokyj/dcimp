package cz.jerzy.dcimp;

import java.util.Set;

/**
 * Imported media type.
 */
public enum MediaType {
    NEF,
    ARW,
    JPG;

    private static final Set<MediaType> IMAGES = Set.of(NEF, ARW, JPG);
    private static final Set<MediaType> RAW_IMAGES = Set.of(NEF, ARW);

    public boolean isImage() {
        return IMAGES.contains(this);
    }

    public boolean isRawImage() {
        return RAW_IMAGES.contains(this);
    }

}
