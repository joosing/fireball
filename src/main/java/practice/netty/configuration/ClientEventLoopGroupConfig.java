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
public class ClientEventLoopGroupConfig {
    @Value("${fireball.client.thread.max.io}")
    private Integer nChannelIoThread;
    @Value("${fireball.client.thread.max.file.store}")
    private Integer nFileStoreThread;

    @Getter private EventLoopGroup channelIoEventLoopGroup;
    @Getter private EventLoopGroup fileStoreEventLoopGroup;

    @PostConstruct
    void setUp() {
        channelIoEventLoopGroup = new NioEventLoopGroup(nChannelIoThread);
        fileStoreEventLoopGroup = new DefaultEventLoopGroup(nFileStoreThread);
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        channelIoEventLoopGroup.shutdownGracefully().sync();
        fileStoreEventLoopGroup.shutdownGracefully().sync();
    }
}
