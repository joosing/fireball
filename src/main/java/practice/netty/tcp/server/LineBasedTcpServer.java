package practice.netty.tcp.server;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.outbound.LineAppender;
import practice.netty.tcp.common.Handler;

import java.util.ArrayList;
import java.util.List;

public class LineBasedTcpServer extends AbstractCustomServer {

    @Override
    protected List<Handler> configChildHandlers() {
        List<Handler> childHandlers = new ArrayList<>();
        childHandlers.add(Handler.of(new LineBasedFrameDecoder(1024)));
        childHandlers.add(Handler.of(new StringDecoder()));
        childHandlers.add(Handler.of(new StringEncoder()));
        childHandlers.add(Handler.of(new LineAppender("\n")));
        return childHandlers;
    }
}
