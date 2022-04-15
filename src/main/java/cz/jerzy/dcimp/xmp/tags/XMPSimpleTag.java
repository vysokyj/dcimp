package cz.jerzy.dcimp.xmp.tags;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import cz.jerzy.dcimp.xmp.XMPTag;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XMPSimpleTag implements XMPTag {

    private String ns;
    private String key;
    private String value;

    @Override
    public void write(XMPMeta xmpMeta) throws XMPException {
        xmpMeta.setProperty(ns, key, value);
    }

    @Override
    public String getPrefix() {
        switch (getNs()) {
            case XMPConst.NS_TIFF:
                return "tiff";
            case XMPConst.NS_EXIF:
                return "exif";
            case XMPConst.NS_XMP:
                return "xmp";
            default:
                throw new IllegalArgumentException("Unsupported namespace " + getNs());
        }
    }

    @Override
    public String getFullKey() {
        return getPrefix() + ":" + getKey();
    }

    @Override
    public int compareTo(XMPTag o) {
        return this.getFullKey().compareTo(o.getFullKey());
    }
}
