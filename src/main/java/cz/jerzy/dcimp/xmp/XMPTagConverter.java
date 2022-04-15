package cz.jerzy.dcimp.xmp;

import com.drew.metadata.Tag;

import java.util.Set;

public interface XMPTagConverter {

    String getDirectoryName();

    String getTagName();

    Set<XMPTag> convert(Tag tag);

}
