package cz.jerzy.dcimp.xmp;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;

public interface XMPTag extends Comparable<XMPTag> {

    String getNs();

    String getPrefix();

    String getKey();

    String getFullKey();

    void write(XMPMeta xmpMeta) throws XMPException;

}
