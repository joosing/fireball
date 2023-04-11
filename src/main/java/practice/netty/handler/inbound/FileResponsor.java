package practice.netty.handler.inbound;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import practice.netty.message.FileFetchRegionResponse;
import practice.netty.message.FileFetchRequest;
import practice.netty.message.FileFetchResponse;
import practice.netty.message.Message;
import practice.netty.util.NettyFileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileResponsor extends SimpleChannelInboundHandler<Message> {
    private final String rootPath;
    private final Map<Class<? extends Message>, ResponseFactory> responseFactoryMap;

    public FileResponsor(String rootPath) {
        this.rootPath = rootPath;
        responseFactoryMap = new HashMap<>();
        configHandleMap();
    }

    private void configHandleMap() {
        responseFactoryMap.put(FileFetchRequest.class, this::responseToFileFetchRequestManually);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        responseFactoryMap.get(message.getClass())
                .response(message, ctx.alloc())
                .ifPresent(response -> ctx.writeAndFlush(response));
    }

    /**
     * 파일 패치 요청에 FileRegion 매커니즘을 활용해 응답합니다. (zero-copy)
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return FileRegion을 담은 응답
     */
    private Optional<Message> responseToFileFetchRequest(Message request, ByteBufAllocator allocator) {
        var fileFetchRequest = (FileFetchRequest) request;
        var actualPath = rootPath + fileFetchRequest.getRemoteFilePath();
        var response = FileFetchRegionResponse.builder()
                .filePath(actualPath)
                .build();
        return Optional.of(response);
    }

    /**
     * 파일 패치 요청에 직접 파일을 읽어서 응답합니다.
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return 파일 컨텐츠를 담은 응답
     */
    private Optional<Message> responseToFileFetchRequestManually(Message request, ByteBufAllocator allocator) throws IOException {
        var fileFetchRequest = (FileFetchRequest) request;
        var path = rootPath + fileFetchRequest.getRemoteFilePath();
        var contents = NettyFileUtils.readAllBytes(path, allocator);
        var response = FileFetchResponse.builder()
                .fileContents(contents)
                .build();
        return Optional.of(response);
    }
}
