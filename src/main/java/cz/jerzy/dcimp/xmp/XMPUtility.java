package cz.jerzy.dcimp.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.impl.XMPMetaImpl;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.xmp.XmpDirectory;
import cz.jerzy.dcimp.xmp.convertors.*;

import java.util.*;

// http://www.npes.org/pdf/xmpspecification-Jun05.pdf
// https://exiv2.org/conversion.html
// https://exiftool.org/TagNames/XMP.html
public class XMPUtility {

    private final Set<XMPTagConverter> converters = Set.of(
            new ExifCompression(),
            new ExifMake(),
            new ExifModel(),
            new ExifSubFNumber(),
            new ExifOrientation(),
            new ExifSubExifImageHeight(),
            new ExifSubExifImageWidth(),
            new ExifXResolution(),
            new ExifYResolution(),
            new ExifSoftware(),
            new SonyMakernoteRating()
    );

    /**
     * Converts internal metadata to XMP metadata.
     *
     * @param metadata       internal metadata
     * @param additionalTags additional tags
     * @return XMP metadata
     */
    public Metadata convert(Metadata metadata, Set<XMPTag> additionalTags) {
        SortedSet<XMPTag> xmpTags = extractXMPTags(metadata);
        xmpTags.addAll(additionalTags);
        final XMPMeta xmpMeta = new XMPMetaImpl();
        xmpTags.forEach(xmpTag -> write(xmpMeta, xmpTag));
        final XmpDirectory xmpDirectory = new XmpDirectory();
        xmpDirectory.setXMPMeta(xmpMeta);
        final Metadata xmpMetadata = new Metadata();
        xmpMetadata.addDirectory(xmpDirectory);
        return xmpMetadata;
    }

    private void write(XMPMeta xmpMeta, XMPTag xmpTag) {
        try {
            xmpTag.write(xmpMeta);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SortedSet<XMPTag> extractXMPTags(Metadata metadata) {
        SortedSet<XMPTag> set = new TreeSet<>();
        metadata.getDirectories()
                .forEach(directory -> directory.getTags()
                        .forEach(tag -> getOptionalTagConverter(tag)
                                .ifPresent(converter -> set.addAll(converter.convert(tag)))));
        return set;
    }

    private Optional<XMPTagConverter> getOptionalTagConverter(Tag tag) {
        return converters.stream()
                .filter(converter -> converter.getDirectoryName().equals(tag.getDirectoryName()))
                .filter(converter -> converter.getTagName().equals(tag.getTagName()))
                .findFirst();
    }

}
