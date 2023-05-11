package practice.netty.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import practice.netty.util.AdvancedFileUtils;

import java.io.IOException;
import java.util.stream.IntStream;

import static practice.netty.util.FileSizeUtils.megaToByte;

@SuppressWarnings("NewClassNamingConvention")
@Disabled("This is not a test class")
public class FileServerTestUtils {
    static final String localFileFormat = "local%d.dat";
    static final String remoteFileFormat = "remote%d.dat";
    static final int nFile = 1024;
    static final int megaBytes = 10;

    @Test
    void newRemoteFile() throws IOException {
        AdvancedFileUtils.newRandomContentsFile("remote-1000.dat", megaToByte(1024));
    }

    @Test
    void newRemoteFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.newRandomContentsFile(remoteFileFormat.formatted(i), megaToByte(megaBytes));
        });
    }

    @Test
    void deleteRemoteFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(remoteFileFormat.formatted(i));
        });
    }

    @Test
    void deleteLocalFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(localFileFormat.formatted(i));
        });
    }
}
