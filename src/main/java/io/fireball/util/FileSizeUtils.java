package io.fireball.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileSizeUtils {
    public static final int KB = 1024;
    public static final int MB = KB * KB;
    public static final int GB = KB * KB * KB;
    public static int megaToByte(int mb) {
        return mb * KB * KB;
    }
}
