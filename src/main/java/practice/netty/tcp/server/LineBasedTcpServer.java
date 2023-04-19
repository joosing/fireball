package practice.netty.tcp.server;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.handler.outbound.LineAppender;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.ArrayList;
import java.util.List;

public class LineBasedTcpServer extends AbstractCustomServer {

    @Override
    protected List<HandlerWorkerPair> configChildHandlers() {
        List<HandlerWorkerPair> childHandlers = new ArrayList<>();
        childHandlers.add(
                HandlerWorkerPair.of(() -> new LineBasedFrameDecoder(1024)));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new StringDecoder()));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new StringEncoder()));
        childHandlers.add(
                HandlerWorkerPair.of(() -> new LineAppender("\n")));
        return childHandlers;
    }
}
