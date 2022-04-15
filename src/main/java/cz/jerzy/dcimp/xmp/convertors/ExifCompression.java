package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import cz.jerzy.dcimp.xmp.XMPTag;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class ExifCompression implements XMPTagConverter {

    @Override
    public String getDirectoryName() {
        return "Exif IFD0";
    }

    @Override
    public String getTagName() {
        return "Compression";
    }

    @Override
    public Set<XMPTag> convert(Tag tag) {
        return Set.of(XMPSimpleTag.builder()
                .ns(XMPConst.NS_TIFF)
                .key("FileType")
                .value(getValue(tag))
                .build());
    }

    private String getValue(Tag tag) {
        return tag.getDescription().replace(" (old-style)", "");
    }

}
