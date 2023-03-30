package practice.netty.tcp.client;

import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultCustomClient extends AbstractCustomClient {

    @Override
    protected List<ChannelHandler> configHandlers() {
        // 비어있는 채널 파이프라인 핸들러 구성
        return new ArrayList<>();
    }
}
