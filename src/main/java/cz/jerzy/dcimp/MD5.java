package cz.jerzy.dcimp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;

import lombok.experimental.UtilityClass;

import static cz.jerzy.dcimp.FileSystem.requireRegularFile;

@UtilityClass
public class MD5 {

    static public String calculateChecksum(Path path) {
        byte[] digest = digest(requireRegularFile(path).toFile());
        StringBuilder result = new StringBuilder();
        for (byte value : digest)
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    static public boolean verifyChecksum(Path path, String checksum) {
        return calculateChecksum(path).equals(checksum);
    }

    static private byte[] digest(File file) {
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0)
                    complete.update(buffer, 0, numRead);
            } while (numRead != -1);
            return complete.digest();
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }
}
