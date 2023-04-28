package practice.netty.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import practice.netty.message.FileFetchRxResponse;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

@RequiredArgsConstructor
public class FileStoreHandler extends SimpleChannelInboundHandler<FileFetchRxResponse> {
    private final String storePath;
    private final FileDownloadCompleteListener storeCompleteListener;

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
    protected void channelRead0(ChannelHandlerContext ctx, FileFetchRxResponse response) throws Exception {
        // 파일에 저장
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(storePath, true))) {

            final ByteBuf fileContents = response.fileContents();
            final int requestRead = fileContents.readableBytes();

            fileContents.readBytes(outputStream, requestRead);
        }

        if (response.endOfFile()) {
            // 저장 완료 알림
            storeCompleteListener.fileDownloadComplete(storePath);
        }
    }
}
