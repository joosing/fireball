package practice.netty.eventloop;

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
public class ClientEventLoopGroupManager {
    @Value("${fireball.client.thread.max.io}")
    private Integer nChannelIoThread;
    @Value("${fireball.client.thread.max.file.store}")
    private Integer nFileStoreThread;

    @Getter private EventLoopGroup channelIo;
    @Getter private EventLoopGroup fireStore;

    @PostConstruct
    void setUp() {
        channelIo = new NioEventLoopGroup(nChannelIoThread);
        fireStore = new DefaultEventLoopGroup(nFileStoreThread);
    }

    @PreDestroy
    void tearDown() throws InterruptedException {
        channelIo.shutdownGracefully().sync();
        fireStore.shutdownGracefully().sync();
    }
}
