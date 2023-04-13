package practice.netty.handler.inbound;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import practice.netty.message.FileFetchRegionResponse;
import practice.netty.message.FileFetchRequest;
import practice.netty.message.FileFetchResponse;
import practice.netty.message.Message;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.EMPTY_BUFFER;

public class FileResponsor extends SimpleChannelInboundHandler<Message> {
    private final String rootPath;
    private final Map<Class<? extends Message>, ResponseFunction> responseFunctionMap;

    public FileResponsor(String rootPath) {
        this.rootPath = rootPath;
        responseFunctionMap = new HashMap<>();
        configHandleMap();
    }

    private void configHandleMap() {
        responseFunctionMap.put(FileFetchRequest.class, this::responseFileFetchRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
/*        responseFunctionMap.get(message.getClass())
                .response(message, ctx.alloc())
                .forEach(ctx::writeAndFlush);*/
        final int chunkSize = 1024 * 1024 * 5;
        var fileFetchRequest = (FileFetchRequest) message;
        var file = new File(rootPath + fileFetchRequest.getRemoteFilePath());
        long remainBytes = file.length();
        long start = 0;
        while(remainBytes > 0 ) {
            var readBytes = (int) Math.min(remainBytes, chunkSize);
            var chunk = FileFetchRegionResponse.builder()
                    .endOfFile(false)
                    .filePath(file.getPath())
                    .start(start)
                    .length(readBytes)
                    .build();
            ctx.writeAndFlush(chunk);
            remainBytes -= readBytes;
            start += readBytes;
        }
        var chunk = FileFetchResponse.builder()
                .endOfFile(true)
                .fileContents(EMPTY_BUFFER)
                .build();
        ctx.writeAndFlush(chunk);
    }

    /**
     * 파일 패치 요청에 FileRegion 매커니즘을 활용해 응답합니다. (zero-copy)
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return FileRegion을 담은 응답
     */
    private List<Message> responseFileFetchRequest(Message request, ByteBufAllocator allocator) {
        var fileFetchRequest = (FileFetchRequest) request;
        var actualPath = rootPath + fileFetchRequest.getRemoteFilePath();
        var response = FileFetchRegionResponse.builder()
                .filePath(actualPath)
                .build();
        return List.of(response);
    }

    /**
     * 파일 패치 요청에 직접 파일을 읽어서 응답합니다. 이 메서드는 Deprecated 되었습니다. 직접 파일을 읽어서 전송하는 것 보다 FileRegion을
     * 활용해서 전송하는 것이 대용량 파일 전송 시 더 적은 메모리를 사용하고, 속도도 더 빠릅니다.
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return 파일 컨텐츠를 담은 응답
     */
/*    @Deprecated

    private List<Message> responseFileFetchRequestByDirect(ChannelHandlerContext ctx, Message request, ByteBufAllocator allocator) throws IOException {
        final int chunkSize = 1024 * 5;
        var fileFetchRequest = (FileFetchRequest) request;
        var chunks = new ArrayList<Message>();
        var file = new File(rootPath + fileFetchRequest.getRemoteFilePath());
        long remainBytes = file.length();
        long start = 0;
        while(remainBytes > 0 ) {
            var contents = allocator.directBuffer(chunkSize);
            NettyFileUtils.readRandomAccess(file, start, chunkSize, contents);
            var chunk = FileFetchResponse.builder()
                    .fileContents(contents)
                    .build();
            ctx.writeAndFlush(chunk);
            remainBytes -= chunkSize;
            start += chunkSize;
        }
        return chunks;
    }
*/

    private static void checkNettyByteBufCapacity(long size) {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Too large file: " + size + " bytes");
        }
    }
}
