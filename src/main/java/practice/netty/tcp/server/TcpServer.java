package practice.netty.tcp.server;

import io.netty.channel.EventLoopGroup;
import practice.netty.handler.inbound.ReadDataListener;
import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

public interface TcpServer extends TcpServerRequest, ReadDataListener, ClientActiveListener, UnsafeMultiChannelAccess {
    void init(EventLoopGroup bossGroup, EventLoopGroup workGroup, List<HandlerWorkerPair> childHandlers);
}
