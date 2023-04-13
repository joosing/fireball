package practice.netty.util;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;

import java.io.*;

@UtilityClass
public class NettyFileUtils {
    /**
     * 파일의 전체 내용을 ByteBuf에 씁니다.
     * @param srcFile 소스 파일
     * @param dstBuffer 목적지 버퍼
     */
    public static void readAllBytes(File srcFile, ByteBuf dstBuffer) throws IOException {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(srcFile))) {
            dstBuffer.writeBytes(inputStream, (int) srcFile.length());
        }
    }

    public static void readRandomAccess(File srcFile, long start, int length, ByteBuf dstBuffer) throws IOException {
        final RandomAccessFile raf = new RandomAccessFile(srcFile, "r");
        raf.seek(start);
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(raf.getFD()))) {
            dstBuffer.writeBytes(inputStream, length);
        }
    }
}
