package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;

import java.util.Collections;
import java.util.Set;

public class ExifIFD0DirectoryConvertor extends XMPTagConverter<ExifIFD0Directory> {

    @Override
    public Set<XMPTag> convert(ExifIFD0Directory directory, Tag tag) {
        switch (tag.getTagType()) {
            case ExifDirectoryBase.TAG_FNUMBER:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("FNumber")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_COMPRESSION:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("FileType")
                        .value(getFileTypeValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_ORIENTATION:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key(tag.getTagName())
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("ImageLength")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("ImageWidth")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_X_RESOLUTION:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("XResolution")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_Y_RESOLUTION:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("YResolution")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifDirectoryBase.TAG_MAKE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("Make")
                        .value(tag.getDescription())
                        .build());
            case ExifDirectoryBase.TAG_MODEL:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("Model")
                        .value(tag.getDescription())
                        .build());
            case ExifDirectoryBase.TAG_SOFTWARE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("CreatorTool")
                        .value(tag.getDescription())
                        .build());
            case ExifDirectoryBase.TAG_RESOLUTION_UNIT:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("ResolutionUnit")
                        .value(getLongValue(directory, tag))
                        .build());
            default:
                return Collections.emptySet();
        }
    }

    @Nullable
    public String getFileTypeValue(ExifIFD0Directory directory, Tag tag) {
        Integer value = directory.getInteger(tag.getTagType());
        if (value == null)
            return null;
        // TODO Upgrade by specification
        switch (value) {
            case 1:
                return "Uncompressed";
            case 2:
                return "CCITT 1D";
            case 3:
                return "T4/Group 3 Fax";
            case 4:
                return "T6/Group 4 Fax";
            case 5:
                return "LZW";
            case 6:
                return "JPEG"; // changesd
            case 7:
                return "JPEG";
            case 8:
                return "Adobe Deflate";
            case 9:
                return "JBIG B&W";
            case 10:
                return "JBIG Color";
            case 99:
                return "JPEG";
            case 262:
                return "Kodak 262";
            case 32766:
                return "Next";
            case 32767:
                return "Sony ARW Compressed";
            case 32769:
                return "Packed RAW";
            case 32770:
                return "Samsung SRW Compressed";
            case 32771:
                return "CCIRLEW";
            case 32772:
                return "Samsung SRW Compressed 2";
            case 32773:
                return "PackBits";
            case 32809:
                return "Thunderscan";
            case 32867:
                return "Kodak KDC Compressed";
            case 32895:
                return "IT8CTPAD";
            case 32896:
                return "IT8LW";
            case 32897:
                return "IT8MP";
            case 32898:
                return "IT8BL";
            case 32908:
                return "PixarFilm";
            case 32909:
                return "PixarLog";
            case 32946:
                return "Deflate";
            case 32947:
                return "DCS";
            case 34661:
                return "JBIG";
            case 34676:
                return "SGILog";
            case 34677:
                return "SGILog24";
            case 34712:
                return "JPEG 2000";
            case 34713:
                return "Nikon NEF Compressed";
            case 34715:
                return "JBIG2 TIFF FX";
            case 34718:
                return "Microsoft Document Imaging (MDI) Binary Level Codec";
            case 34719:
                return "Microsoft Document Imaging (MDI) Progressive Transform Codec";
            case 34720:
                return "Microsoft Document Imaging (MDI) Vector";
            case 34892:
                return "Lossy JPEG";
            case 65000:
                return "Kodak DCR Compressed";
            case 65535:
                return "Pentax PEF Compressed";
            default:
                return "Unknown (" + value + ")";
        }
    }


}
