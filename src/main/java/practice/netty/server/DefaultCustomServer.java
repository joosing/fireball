package practice.netty.server;

import practice.netty.common.HandlerFactory;

import java.util.List;

public class DefaultCustomServer extends AbstractCustomServer {
    @Override
    protected void configChildHandlers(List<HandlerFactory> pipelineFactory) {
        // 비어있는 채널 파이프라인 핸들러 구성
    }
}
