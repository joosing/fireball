package practice.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import practice.netty.tcp.handler.inbound.ReadDataUpdater;
import practice.netty.tcp.handler.outbound.LineAppender;

import java.util.concurrent.ExecutionException;

public class LineBasedClient extends DefaultCustomClient {

    public static CustomClient newConnection(String ip, int port, EventLoopGroup eventLoopGroup) throws ExecutionException,
            InterruptedException {
        CustomClient client = new LineBasedClient();
        client.init(eventLoopGroup);
        client.connect(ip, port).get();
        return client;
    }

    @Override
    public void init(EventLoopGroup eventLoopGroup) {
        // Custom 채널 파이프라인 구성
        ChannelInitializer<Channel> channelInitializer = new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline()
                        // Inbound
                        .addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new ReadDataUpdater(LineBasedClient.this))
                        // Outbound
                        .addLast(new StringEncoder())
                        .addLast(new LineAppender("\n"));
            }
        };

        init(eventLoopGroup, channelInitializer);
    }
}
