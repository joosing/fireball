package io.fireball.util;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public final class PropagateChannelFuture {

    public static void propagate(ChannelFuture channelFuture, CompletableFuture<Void> userFuture) {
        // 채널에서 예외가 발생한 경우
        if (channelFuture.cause() != null) {
            userFuture.completeExceptionally(channelFuture.cause());
            return;
        }

        // 작업이 취소된 경우
        if (channelFuture.isCancelled()) {
            userFuture.completeExceptionally(new CancellationException());
            return;
        }

        // 작업이 실행 완료된 경우
        userFuture.complete(null);
    }

    private PropagateChannelFuture() {}
}
