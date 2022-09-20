package cz.jerzy.dcimp.xmp;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import lombok.SneakyThrows;

import java.util.Set;

public abstract class XMPTagConverter<T> {

    public abstract Set<XMPTag> convert(T directory, Tag tag);

    @SneakyThrows
    protected String getDoubleValue(Directory directory, Tag tag) {
        return Double.toString(directory.getDouble(tag.getTagType()));
    }

    @SneakyThrows
    protected String getLongValue(Directory directory, Tag tag) {
        return Long.toString(directory.getLong(tag.getTagType()));
    }

    @SneakyThrows
    protected String getIntegerValue(Directory directory, Tag tag) {
        return Integer.toString(directory.getInteger(tag.getTagType()));
    }

    @SneakyThrows
    protected String getRationalValue(Directory directory, Tag tag) {
        return directory.getRational(tag.getTagType()).toString();
    }

    @SneakyThrows
    protected String getStringValue(Directory directory, Tag tag) {
        return directory.getString(tag.getTagType());
    }

}
