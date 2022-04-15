package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class SonyMakernoteRating implements XMPTagConverter {

    @Override
    public String getDirectoryName() {
        return "Sony Makernote";
    }

    @Override
    public String getTagName() {
        return "Rating";
    }

    @Override
    public Set<XMPTag> convert(Tag tag) {
        return Set.of(XMPSimpleTag.builder()
                .ns(XMPConst.NS_XMP)
                .key("Rating")
                .value(tag.getDescription())
                .build());
    }

}
