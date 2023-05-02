package practice.netty.handler.inbound;

import java.util.concurrent.ExecutionException;

/**
 * 이 인터페이스는 누가 변경의 주도권을 가지는가?
 */
@FunctionalInterface
public interface FileDownloadCompleteListener {
    void fileDownloadComplete(String localFilePath) throws ExecutionException, InterruptedException;
}
