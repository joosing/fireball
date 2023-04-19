package practice.netty.tcp.client;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.outbound.LineAppender;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

public class LineBasedClient extends AbstractCustomClient {

    @Override
    protected void configHandlers(List<HandlerWorkerPair> handlers) {
        handlers.add(
                HandlerWorkerPair.of(() -> new LineBasedFrameDecoder(1024)));
        handlers.add(
                HandlerWorkerPair.of(() -> new StringDecoder()));
        handlers.add(
                HandlerWorkerPair.of(() -> new StringEncoder()));
        handlers.add(
                HandlerWorkerPair.of(() -> new LineAppender("\n")));
    }
}
