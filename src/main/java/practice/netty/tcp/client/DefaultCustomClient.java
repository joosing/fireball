package practice.netty.tcp.client;

import practice.netty.tcp.common.Handler;

import java.util.List;

public class DefaultCustomClient extends AbstractCustomClient {

    @Override
    protected void configHandlers(List<Handler> handlers) {
        // 비어있는 채널 파이프라인 핸들러 구성
    }
}
