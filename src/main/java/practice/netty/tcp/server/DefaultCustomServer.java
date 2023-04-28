package practice.netty.tcp.server;

import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.List;

public class DefaultCustomServer extends AbstractCustomServer {
    @Override
    protected void configChildHandlers(List<HandlerWorkerPair> childHandlers) {
        // 비어있는 채널 파이프라인 핸들러 구성
    }
}
