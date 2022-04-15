package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class ExifSoftware implements XMPTagConverter {

    @Override
    public String getDirectoryName() {
        return "Exif IFD0";
    }

    @Override
    public String getTagName() {
        return "Software";
    }

    @Override
    public Set<XMPTag> convert(Tag tag) {
        return Set.of(XMPSimpleTag.builder()
                .ns(XMPConst.NS_XMP)
                .key("CreatorTool")
                .value(tag.getDescription())
                .build());
    }

}
