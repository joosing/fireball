package practice.netty.server;

import io.netty.channel.EventLoopGroup;
import practice.netty.common.HandlerWorkerPair;
import practice.netty.handler.inbound.ReadDataListener;

import java.util.List;

public interface TcpServer extends TcpServerRequest, ReadDataListener, ClientActiveListener, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup, List<HandlerWorkerPair> childHandlers);
}
