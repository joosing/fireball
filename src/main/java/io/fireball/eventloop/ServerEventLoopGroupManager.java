package io.fireball.eventloop;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Accessors(fluent = true)
public class ServerEventLoopGroupManager {
    @Value("${fireball.server.thread.max.boss}")
    private Integer maxBossThread;
    @Value("${fireball.server.thread.max.io}")
    private Integer maxIoThread;
    @Value("${fireball.server.thread.max.file.store}")
    private Integer maxFileStoreThread;

    @Getter private EventLoopGroup channelIo;
    @Getter private EventLoopGroup boss;
    @Getter private EventLoopGroup fireStore;

    @PostConstruct
    void setUp() {
        boss = new NioEventLoopGroup(maxBossThread);
        channelIo = new NioEventLoopGroup(maxIoThread);
        fireStore = new DefaultEventLoopGroup(maxFileStoreThread);
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        channelIo.shutdownGracefully().sync();
        boss.shutdownGracefully().sync();
        fireStore.shutdownGracefully().sync();
    }
}
