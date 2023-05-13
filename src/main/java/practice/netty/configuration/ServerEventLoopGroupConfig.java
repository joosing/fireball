package practice.netty.configuration;

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
public class ServerEventLoopGroupConfig {
    @Value("${fireball.server.thread.max.boss}")
    private Integer maxBossThread;
    @Value("${fireball.server.thread.max.io}")
    private Integer maxIoThread;
    @Value("${fireball.server.thread.max.file.store}")
    private Integer maxFileStoreThread;

    @Getter private EventLoopGroup ioEventLoopGroup;
    @Getter private EventLoopGroup bossEventLoopGroup;
    @Getter private EventLoopGroup fileStoreEventLoopGroup;

    @PostConstruct
    void setUp() {
        bossEventLoopGroup = new NioEventLoopGroup(maxBossThread);
        ioEventLoopGroup = new NioEventLoopGroup(maxIoThread);
        fileStoreEventLoopGroup = new DefaultEventLoopGroup(maxFileStoreThread);
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        ioEventLoopGroup.shutdownGracefully().sync();
        bossEventLoopGroup.shutdownGracefully().sync();
        fileStoreEventLoopGroup.shutdownGracefully().sync();
    }
}
