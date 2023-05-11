package practice.netty.handler.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.netty.message.ResponseMessage;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class ResponseSupplier extends SimpleChannelInboundHandler<ResponseMessage> {
    private final Consumer<ResponseMessage> responseHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        if (responseHandler != null) {
            responseHandler.accept(msg);
        }
    }
}
