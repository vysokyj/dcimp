package cz.jerzy.dcimp.xmp;

import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.impl.XMPMetaImpl;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import cz.jerzy.dcimp.xmp.convertors.ExifIFD0DirectoryConvertor;
import cz.jerzy.dcimp.xmp.convertors.ExifSubIFDDirectoryConvertor;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

// http://www.npes.org/pdf/xmpspecification-Jun05.pdf
// https://exiv2.org/conversion.html
// https://exiftool.org/TagNames/XMP.html
public class XMPUtility {

    private final ExifIFD0DirectoryConvertor exifIFD0DirectoryConvertor = new ExifIFD0DirectoryConvertor();
    private final ExifSubIFDDirectoryConvertor exifSubIFDDirectoryConvertor = new ExifSubIFDDirectoryConvertor();

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
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (directory instanceof ExifIFD0Directory) {
                    set.addAll(exifIFD0DirectoryConvertor.convert((ExifIFD0Directory) directory, tag));
                }
                if (directory instanceof ExifSubIFDDirectory) {
                    set.addAll(exifSubIFDDirectoryConvertor.convert((ExifSubIFDDirectory) directory, tag));
                }
            }
        }
        return set;
    }

}
