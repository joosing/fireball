package practice.netty.tcp.util;

import io.netty.channel.ChannelFuture;

import java.util.concurrent.CompletableFuture;

public final class PropagateChannelFuture {

    /**
     * 네티 채널에서 발생한 비동기 작업 결과를 사용자에게 전파합니다.
     *
     * 네티 채널에서 사용하는 Future 객체와 사용자에게 반환하는 Future 객체를 분리하는 이유는 다음과 같습니다.
     * - 네티 채널에서 사용하는 Future 객체는 TcpClient의 상태가 안정화 되기 전에(예를 들면 TcpClient가 Channel 참조를 저장하기 전 미묘한
     *   타이밍에) 사용자에게 채널이 사용 가능하다는 상태를 전파할 수 있기 때문입니다. 따라서 TcpClient가 Channel이 아니라 자신이 사용 가능한
     *   상태가 된 경우에 결과를 전달할 수 있도록 합니다.
     *
     * 구현 중에 주의할 점은 CompletableFuture 객체는 계산을 완료한 후 complete() 메서드 또는 completeExceptionally() 메서드 중 하나만
     * 으로 완료 상태로 바뀔 수 있도록 허용한다는 것입니다. 예를 들면 채널에서 예외가 발생했다고 해서 complete(false)라고 설정한 후 다시 예외
     * 정보를 completeExceptionally(channelFuture.cause())로 설정하면 뒤에 설정한 예외는 무시됩니다.
     *
     * @param userFuture 사용자에게 반환된 Future 객체
     * @param channelFuture 네티 채널 내부에서 사용하는 Future 객체
     */
    public static void propagateChannelFuture(CompletableFuture<Boolean> userFuture, ChannelFuture channelFuture) {
        // 채널에서 예외가 발생한 경우
        if (channelFuture.cause() != null) {
            userFuture.completeExceptionally(channelFuture.cause());
            return;
        }

        // 작업이 취소된 경우
        if (channelFuture.isCancelled()) {
            userFuture.complete(false);
            return;
        }

        // 작업이 실행 완료된 경우
        userFuture.complete(channelFuture.isSuccess());
    }

    private PropagateChannelFuture() {}
}
