package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import practice.netty.message.FileFetchRegionResponse;
import practice.netty.message.FileFetchRequest;
import practice.netty.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FileResponsor extends SimpleChannelInboundHandler<Message> {
    private final String rootPath;
    private final Map<Class<? extends Message>, Function<Message, Optional<Message>>> handlerMap;

    public FileResponsor(String rootPath) {
        this.rootPath = rootPath;
        handlerMap = new HashMap<>();
        configHandleMap();
    }

    private void configHandleMap() {
        handlerMap.put(FileFetchRequest.class, this::processFileFetchRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        handlerMap.get(message.getClass())
                .apply(message)
                .ifPresent(response -> ctx.writeAndFlush(response));
    }

    private Optional<Message> processFileFetchRequest(Message request) {
        var fileFetchRequest = (FileFetchRequest) request;
        var actualPath = rootPath + fileFetchRequest.getRemoteFilePath();
        var response = FileFetchRegionResponse.builder()
                .filePath(actualPath)
                .build();
        return Optional.of(response);
    }

}
