package practice.netty.tcp.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;

import java.util.function.Supplier;

/**
 * 네티 파이프라인에 등록할 채널 핸들러 제공자와 이벤트 루프 그룹을 관리합니다.
 * 채널 핸들러는 공유 가능한(@Sharable) 상태가 아니라면 채널에 등록되기 전에 항상 새롭게 생성되어야 합니다.
 * 따라서 생성된 인스턴스 대신 인스턴스를 생성하는 제공자를 설정으로 제공합니다.
 */
public final class HandlerWorkerPair {
    private final Supplier<ChannelHandler> handlerSupplier;
    private EventLoopGroup workGroup;

    public static HandlerWorkerPair of(Supplier<ChannelHandler> channelHandler) {
        return new HandlerWorkerPair(channelHandler);
    }

    public static HandlerWorkerPair of(EventLoopGroup workGroup, Supplier<ChannelHandler> channelHandler) {
        return new HandlerWorkerPair(workGroup, channelHandler);
    }

    private HandlerWorkerPair(Supplier<ChannelHandler> handlerSupplier) {
        this.handlerSupplier = handlerSupplier;
    }

    private HandlerWorkerPair(EventLoopGroup workGroup, Supplier<ChannelHandler> handler) {
        this.workGroup = workGroup;
        this.handlerSupplier = handler;
    }

    public EventLoopGroup workGroup() {
        return workGroup;
    }

    public Supplier<ChannelHandler> handler() {
        return handlerSupplier;
    }
}
