package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class DedicatedSimpleInboundHandler<T> extends SimpleChannelInboundHandler<T> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        /*
         * 주의! 해당 핸들러는 파일 쓰기 때문에 블락킹이 발생함으로 채널의 이벤트 루프와 다른 스레드에 의해 실행되어야 합니다.
         * 만일 그렇지 않으면 파일 쓰기 수행 중 채널의 모든 I/O 동작이 블락킹됩니다.
         */
        assert ctx.executor() != ctx.channel().eventLoop();
        super.channelRegistered(ctx);
    }
}
