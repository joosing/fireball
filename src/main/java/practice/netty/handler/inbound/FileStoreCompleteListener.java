package practice.netty.handler.inbound;

/**
 * 이 인터페이스는 누가 변경의 주도권을 가지는가?
 */
@FunctionalInterface
public interface FileStoreCompleteListener {
    void fileStoreComplete(String localFilePath);
}
