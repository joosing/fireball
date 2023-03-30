package practice.netty.tcp.server;

import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DefaultCustomServer extends AbstractCustomServer {
    @Override
    protected List<Supplier<ChannelHandler>> configChildHandlers() {
        // 비어있는 채널 파이프라인 핸들러 구성
        return new ArrayList<>();
    }
}
