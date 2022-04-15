package cz.jerzy.dcimp.xmp.convertors;

import com.adobe.internal.xmp.XMPConst;
import com.drew.metadata.Tag;
import cz.jerzy.dcimp.xmp.XMPTag;
import cz.jerzy.dcimp.xmp.XMPTagConverter;
import cz.jerzy.dcimp.xmp.tags.XMPSimpleTag;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class ExifOrientation implements XMPTagConverter {

    private static final String[] DESCRIPTIONS = {
            "Top, left side (Horizontal / normal)",
            "Top, right side (Mirror horizontal)",
            "Bottom, right side (Rotate 180)",
            "Bottom, left side (Mirror vertical)",
            "Left side, top (Mirror horizontal and rotate 270 CW)",
            "Right side, top (Rotate 90 CW)",
            "Right side, bottom (Mirror horizontal and rotate 90 CW)",
            "Left side, bottom (Rotate 270 CW)"
    };

    @Override
    public String getDirectoryName() {
        return "Exif IFD0";
    }

    @Override
    public String getTagName() {
        return "Orientation";
    }

    @Override
    public Set<XMPTag> convert(Tag tag) {
        return Set.of(XMPSimpleTag.builder()
                .ns(XMPConst.NS_XMP)
                .key(tag.getTagName())
                .value(getValue(tag))
                .build());
    }

    private String getValue(Tag tag) {
        for (int i = 0; i < DESCRIPTIONS.length; i++) {
            String desc = DESCRIPTIONS[i];
            if (desc.equals(tag.getDescription())) {
                return Integer.valueOf(i + 1).toString();
            }
        }
        throw new IllegalArgumentException("Unknown description: " + tag.getDescription());
    }
}
