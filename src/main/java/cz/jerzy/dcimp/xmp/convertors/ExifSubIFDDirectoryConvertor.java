package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.Set;

public class ExifSubIFDDirectoryConvertor extends XMPTagConverter<ExifSubIFDDirectory> {
    // https://exiv2.org/tags.html

    @Override
    @SneakyThrows
    public Set<XMPTag> convert(ExifSubIFDDirectory directory, Tag tag) {
        switch (tag.getTagType()) {
            case ExifSubIFDDirectory.TAG_RESOLUTION_UNIT:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_TIFF)
                        .key("ResolutionUnit")
                        .value(getLongValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_APERTURE: // TODO not found
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("ApertureValue")
                        .value(getDoubleValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_MAX_APERTURE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("MaxApertureValue")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_BITS_PER_SAMPLE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("BitSPerSample")
                        .value(getStringValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_BRIGHTNESS_VALUE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("BrightnessValue")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_CONTRAST:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("Contrast")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_EXPOSURE_BIAS:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("ExposureBiasValue")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_EXPOSURE_MODE:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("ExposureMode")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("ExposureProgram")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_EXPOSURE_TIME:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("ExposureTime")
                        .value(getStringValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_FLASH:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("Flash")
                        .value(getIntegerValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_FOCAL_LENGTH:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("FocalLength")
                        .value(getRationalValue(directory, tag))
                        .build());
            case ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH:
                return Set.of(XMPSimpleTag.builder()
                        .ns(XMPConst.NS_EXIF)
                        .key("FocalLengthIn35mmFilm")
                        .value(getIntegerValue(directory, tag))
                        .build());
            default:
                return Collections.emptySet();
        }
    }
}
