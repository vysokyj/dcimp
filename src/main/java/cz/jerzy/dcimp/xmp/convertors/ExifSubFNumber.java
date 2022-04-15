package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class ExifSubFNumber implements XMPTagConverter {
    @Override
    public String getDirectoryName() {
        return "Exif SubIFD";
    }

    @Override
    public String getTagName() {
        return "F-Number";
    }

    @Override
    public Set<XMPTag> convert(Tag tag) {
        return Set.of(XMPSimpleTag.builder()
                .ns(XMPConst.NS_TIFF)
                .key("FNumber")
                .value(getValue(tag))
                .build());
    }
    // f/5,6 -> 56/10
    private String getValue(Tag tag) {
        String s = tag.getDescription().replace("f/", "");
        s = s.replace(",", "");
        return s + "/10";
    }
}
