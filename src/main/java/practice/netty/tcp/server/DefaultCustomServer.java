package practice.netty.tcp.server;

import practice.netty.tcp.common.HandlerWorkerPair;

import java.util.ArrayList;
import java.util.List;

public class DefaultCustomServer extends AbstractCustomServer {
    @Override
    protected List<HandlerWorkerPair> configChildHandlers() {
        // 비어있는 채널 파이프라인 핸들러 구성
        return new ArrayList<>();
    }
}
