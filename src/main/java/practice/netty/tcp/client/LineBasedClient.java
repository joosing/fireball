package practice.netty.tcp.client;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.outbound.LineAppender;
import practice.netty.tcp.common.Handler;

import java.util.List;

public class LineBasedClient extends AbstractCustomClient {

    @Override
    protected void configHandlers(List<Handler> handlers) {
        handlers.add(Handler.of(new LineBasedFrameDecoder(1024)));
        handlers.add(Handler.of(new StringDecoder()));
        handlers.add(Handler.of(new StringEncoder()));
        handlers.add(Handler.of(new LineAppender("\n")));
    }
}
