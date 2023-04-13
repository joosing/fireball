package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import practice.netty.message.FileFetchResponse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * 수신된 파일을 저정하는 인바운드 핸들러입니다. 저장 요청이 존재하는 경우 파일을 저장합니다. 이미 파일이 존재하는 경우 파일을 덮어습니다.
 */
public class FileStoreHandler extends SimpleChannelInboundHandler<FileFetchResponse> {
    private volatile FileStoreCompleteListener storeCompleteListener;
    private volatile String storePath;

    // 파일 저장 및 알림 요청 (외부 사용자의 요청)
    public void requestStore(String storePath, FileStoreCompleteListener listener) {
        this.storePath = storePath;
        this.storeCompleteListener = listener;
    }

    // 사용자의 요청 정리 (내부적으로 처리)
    private void clearRequest() {
        storeCompleteListener = null;
        storePath = null;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        /*
         * 주의! 해당 핸들러는 파일 쓰기 때문에 블락킹이 발생함으로 채널의 이벤트 루프와 다른 스레드에 의해 실행되어야 합니다.
         * 만일 그렇지 않으면 파일 쓰기 수행 중 채널의 모든 I/O 동작이 블락킹됩니다.
         */
        assert ctx.executor() != ctx.channel().eventLoop();
        System.out.println(ctx.executor() + " compare to " + ctx.channel().eventLoop());
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileFetchResponse response) throws Exception {
        // 파일 저장 요청 없음
        if (storePath == null) {
            throw new IllegalStateException("storePath is null");
        }

        // 파일에 저장
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(storePath, true))) {

            final ByteBuf fileContents = response.getFileContents();
            final int requestRead = fileContents.readableBytes();

            fileContents.readBytes(outputStream, requestRead);
/*            // 파일 쓰기 실패
            if (requestRead != actualRead) {
                throw new IllegalStateException("fileContents.readBytes() return " + actualRead + " but " +
                requestRead + " is expected.");
            }*/
        }

        if (response.isEndOfFile()) {
            // 저장 완료 알림
            storeCompleteListener.fileStoreComplete(storePath);
            clearRequest();
        }
    }
}
