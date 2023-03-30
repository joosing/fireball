package practice.netty.tcp.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DefaultCustomClient extends AbstractCustomClient {

    public static CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = new DefaultCustomClient();
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    @Override
    protected List<ChannelHandler> configHandlers() {
        // 비어있는 채널 파이프라인 핸들러 구성
        return new ArrayList<>();
    }
}
