package cz.jerzy.dcimp;

import java.nio.file.Path;
import java.util.zip.CRC32;

import org.apache.commons.io.FileUtils;

public class Sfv {
    
    private Path checkedFile;
    private Path sfvFile;
    private String sum;

    /**
     * Crete CSF file.
     * Required checked file or SFV file or both.
     * @param checkedFile checked file
     * @param sfvFile SFV file
     */
    public Sfv(Path checkedFile, Path sfvFile) {
        this.checkedFile = checkedFile;
        this.sfvFile = sfvFile;
        if (checkedFile == null) {

        }
        if (sfvFile == null) {
            recalculate();
        }
    }

    /**
     * Load DCIMP SFV extedned file.
     * @param sfvFile SFV file path
     * @return object representing SFV
     */
    public static Sfv load(Path sfvFile) {
        return new Sfv(null, sfvFile);
    }

    /**
     * Creaet DCIM SFV exetended file.
     * @param checkedFile checked file
     * @return
     */
    public static Sfv calculate(Path checkedFile) {
        return new Sfv(checkedFile, null);
    }


    private void recalculate() {
        try {
            sum = Long.toHexString(FileUtils.checksum(checkedFile.toFile(), new CRC32()).getValue());
        } catch (Exception e) {
            throw new ProcessException(e);
        }
    }

}
