package practice.netty.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@UtilityClass
public final class AdvancedFileUtils {
    /**
     * 사용자가 요청한 컨텐츠를 가진 파일을 생성합니다.
     * @param path 파일 경로
     * @param content 파일 내용
     * @return 생성된 파일
     */
    public static File newTextFile(String path, String content) {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
            return new File(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 랜덤 컨텐츠를 가진 파일을 생성합니다. 호출 할 때 마다 다른 컨텐츠를 생성합니다.
     * @param path 파일 경로
     * @param size 파일 크기
     */
    public static File newRandomContentsFile(String path, int size) {
        // 랜덤 컨텐츠 생성
        byte[] randomContents = new byte[size];
        Random random = new Random();
        random.nextBytes(randomContents);

        // 파일 쓰기
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
                outputStream.write(randomContents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File(path);
    }

    public static boolean contentEquals(String filePath1, String filePath2) {
        try {
            File file1 = new File(filePath1);
            File file2 = new File(filePath2);
            return FileUtils.contentEquals(file1, file2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteIfExists(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
