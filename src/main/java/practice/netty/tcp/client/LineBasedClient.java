package practice.netty.tcp.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LineBasedClient extends AbstractCustomClient {

    public static CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = new LineBasedClient();
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    @Override
    protected List<ChannelHandler> configHandlers() {
        List<ChannelHandler> handlers = new ArrayList<>();
        handlers.add(new LineBasedFrameDecoder(1024)); // 인바운드
        handlers.add(new StringDecoder());
        handlers.add(new StringEncoder()); // 아웃바운드
        handlers.add(new LineAppender("\n"));
        return handlers;
    }
}
