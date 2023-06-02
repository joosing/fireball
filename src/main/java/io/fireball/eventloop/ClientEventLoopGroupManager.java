package io.fireball.eventloop;

import io.fireball.specification.channel.FileClientSpec;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ClientEventLoopGroupManager {
    private final FileClientSpec clientSpec;
    @Getter private EventLoopGroup channelIo;
    @Getter private EventLoopGroup fireStore;

    @PostConstruct
    void setUp() {
        channelIo = new NioEventLoopGroup(clientSpec.nChannelIoMaxThread());
        fireStore = new DefaultEventLoopGroup(clientSpec.nFileStoreMaxThread());
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        channelIo.shutdownGracefully().sync();
        fireStore.shutdownGracefully().sync();
    }
}
