package practice.netty.tcp;

import io.netty.channel.EventLoopGroup;
import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

public interface CustomClient extends DirectTcpOperation, ChannelTestable {
    void init(EventLoopGroup eventLoopGroup);
    @Nullable
    String read() throws InterruptedException;
    @Nullable
    String read(int timeout, TimeUnit unit) throws InterruptedException;
}
