package io.fireball.eventloop;

import io.fireball.specification.channel.FileServerSpec;
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
public class ServerEventLoopGroupManager {
    private final FileServerSpec severSpec;
    @Getter private EventLoopGroup channelIo;
    @Getter private EventLoopGroup boss;
    @Getter private EventLoopGroup fireStore;

    @PostConstruct
    void setUp() {
        boss = new NioEventLoopGroup(severSpec.nBossMaxThread());
        channelIo = new NioEventLoopGroup(severSpec.nChannelIoMaxThread());
        fireStore = new DefaultEventLoopGroup(severSpec.nFileStoreMaxThread());
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        channelIo.shutdownGracefully().sync();
        boss.shutdownGracefully().sync();
        fireStore.shutdownGracefully().sync();
    }
}
