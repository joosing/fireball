package practice.netty.service;

import org.junit.jupiter.api.Test;
import practice.netty.util.AdvancedFileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static practice.netty.util.FileSizeUtils.megaToByte;

@SuppressWarnings("NewClassNamingConvention")
public class FileServerTestUtils {
    static final String localFileFormat = "local%d.dat";
    static final String remoteFileFormat = "remote%d.dat";
    static final int nFile = 1024;
    static final int megaBytes = 5;

    @Test
    void newRemoteFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.newRandomContentsFile(remoteFileFormat.formatted(i), megaToByte(megaBytes));
        });
    }

    @Test
    void deleteRemoteFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(Path.of(remoteFileFormat.formatted(i)));
        });
    }

    @Test
    void deleteLocalFiles() throws IOException {
        IntStream.range(0, nFile).parallel().forEach(i -> {
            AdvancedFileUtils.deleteIfExists(Path.of(localFileFormat.formatted(i)));
        });
    }
}
