package practice.netty.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilTest {
    @Test
    void newRandomContentsFile() throws Exception {
        int size = 1024 * 1024 * 5; // 5MB
        File file = FileUtils.newRandomContentsFile("sample.dat", size);
        assertEquals(size, file.length());
        assertTrue(file.delete());
    }
}
