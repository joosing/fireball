package practice.netty.util;

import java.io.*;
import java.util.Random;

public final class FileUtils {
    private FileUtils() {
    }

    /**
     * 사용자가 요청한 컨텐츠를 가진 파일을 생성합니다.
     * @param path 파일 경로
     * @param content 파일 내용
     * @return 생성된 파일
     * @throws IOException 파일 생성 실패
     */
    public static File newTextFile(String path, String content) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write(content);
            return new File(path);
        }
    }

    /**
     * 랜덤 컨텐츠를 가진 파일을 생성합니다. 호출 할 때 마다 다른 컨텐츠를 생성합니다.
     * @param path 파일 경로
     * @param size 파일 크기
     */
    public static File newRandomContentsFile(String path, int size) throws IOException {
        // 랜덤 컨텐츠 생성
        byte[] randomContents = new byte[size];
        Random random = new Random();
        random.nextBytes(randomContents);

        // 파일 쓰기
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path))) {
            outputStream.write(randomContents);
        }
        return new File(path);
    }
}
