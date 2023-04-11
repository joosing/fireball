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
    private final Map<Class<? extends Message>, ResponseFunction> responseFunctionMap;
    private static final boolean fileRegionFeature = true; // Feature Toggle (FileRegion 사용 여부)

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
        responseFunctionMap.get(message.getClass())
                .response(message, ctx.alloc())
                .ifPresent(response -> ctx.writeAndFlush(response));
    }

    private Optional<Message> responseFileFetchRequest(Message request, ByteBufAllocator allocator) throws IOException {
        if (fileRegionFeature) {
            return implementFileRegionFeature(request, allocator);
        } else {
            return implementDirectReadFeature(request, allocator);
        }
    }

    /**
     * 파일 패치 요청에 FileRegion 매커니즘을 활용해 응답합니다. (zero-copy)
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return FileRegion을 담은 응답
     */
    private Optional<Message> implementFileRegionFeature(Message request, ByteBufAllocator allocator) {
        var fileFetchRequest = (FileFetchRequest) request;
        var actualPath = rootPath + fileFetchRequest.getRemoteFilePath();
        var response = FileFetchRegionResponse.builder()
                .filePath(actualPath)
                .build();
        return Optional.of(response);
    }

    /**
     * 파일 패치 요청에 직접 파일을 읽어서 응답합니다. 이 메서드는 Deprecated 되었습니다. 직접 파일을 읽어서 전송하는 것 보다 FileRegion을
     * 활용해서 전송하는 것이 대용량 파일 전송 시 더 좋은 성능을 보입니다.
     * @param request 파일 패치 요청
     * @param allocator ByteBuf 할당자
     * @return 파일 컨텐츠를 담은 응답
     */
    private Optional<Message> implementDirectReadFeature(Message request, ByteBufAllocator allocator) throws IOException {
        var fileFetchRequest = (FileFetchRequest) request;
        var path = rootPath + fileFetchRequest.getRemoteFilePath();
        var contents = NettyFileUtils.readAllBytes(path, allocator);
        var response = FileFetchResponse.builder()
                .fileContents(contents)
                .build();
        return Optional.of(response);
    }
}
