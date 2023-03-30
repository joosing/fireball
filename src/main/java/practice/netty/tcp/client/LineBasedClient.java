package practice.netty.tcp.client;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.util.ArrayList;
import java.util.List;

public class LineBasedClient extends AbstractCustomClient {

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
