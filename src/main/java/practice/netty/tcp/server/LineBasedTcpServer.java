package practice.netty.tcp.server;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.outbound.LineAppender;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

public class LineBasedTcpServer extends AbstractCustomServer {

    @Override
    protected void configChildHandlers(List<HandlerWorkerPair> childHandlers) {
        // Build up
        List<HandlerWorkerPair> handlerWorkerPairs = List.of(
                // Inbound
                HandlerWorkerPair.of(() -> new LineBasedFrameDecoder(1024)),
                HandlerWorkerPair.of(() -> new StringDecoder()),
                // Outbound
                HandlerWorkerPair.of(() -> new StringEncoder()),
                HandlerWorkerPair.of(() -> new LineAppender("\n")));

        // Add
        childHandlers.addAll(handlerWorkerPairs);
    }
}
