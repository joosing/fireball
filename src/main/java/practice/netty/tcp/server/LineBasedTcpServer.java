package practice.netty.tcp.server;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LineBasedTcpServer extends AbstractCustomServer {

    @Override
    protected List<Supplier<ChannelHandler>> configChildHandlers() {
        List<Supplier<ChannelHandler>> childHandlers = new ArrayList<>();
        childHandlers.add(() -> new LineBasedFrameDecoder(1024));
        childHandlers.add(() -> new StringDecoder());
        childHandlers.add(() -> new StringEncoder());
        childHandlers.add(() -> new LineAppender("\n"));
        return childHandlers;
    }
}
