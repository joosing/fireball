package io.fireball.handler.outbound;

import io.fireball.message.MessageEncodable;
import io.fireball.specification.channel.HeaderSpecProvider;
import io.fireball.specification.message.ProtocolIdProvider;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class MessageEncoder extends ChannelOutboundHandlerAdapter {
    private final ProtocolIdProvider idProvider;
    private final HeaderSpecProvider headerSpecProvider;

    /**
     * Create a message header and body pieces. and then send them all.
     * The reason the body is split into multiple pieces is because of the way FileRegion is handled.
     * When you send a FileRegion, the file content is inserted into the message by a netty channel.
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // Create body peices
        MessageEncodable encodable = (MessageEncodable) msg;
        List<EncodedBodyPiece> bodyPieces = encodable.encode(ctx.alloc().buffer());

        // Create a header
        ByteBuf header = buildHeader(ctx.alloc().buffer(), bodyPieces, encodable.getClass());

        // Send the header
        ctx.write(header);

        // Send all body pieces except the last one
        if (isMultiplePieces(bodyPieces)){
            IntStream.rangeClosed(0, lastIndex(bodyPieces) - 1)
                    .forEach(i -> ctx.write(bodyPieces.get(i).contents()));
        }

        // Send the last body piece with the promise
        ctx.write(bodyPieces.get(lastIndex(bodyPieces)).contents(), promise);
    }

    private ByteBuf buildHeader(ByteBuf header, List<EncodedBodyPiece> bodyPieces, Class<? extends MessageEncodable> clazz) {
        // Get field values
        var length = bodyPieces.stream().mapToInt(EncodedBodyPiece::length).sum() + headerSpecProvider.id().length();
        var id = idProvider.getProtocolId(clazz);
        // Write field values
        headerSpecProvider.length().writeFunc(header, length);
        headerSpecProvider.id().writeFunc(header, id);
        return header;
    }

    private static int lastIndex(List<EncodedBodyPiece> body) {
        return body.size() - 1;
    }

    private static boolean isMultiplePieces(List<EncodedBodyPiece> body) {
        return body.size() > 1;
    }
}
