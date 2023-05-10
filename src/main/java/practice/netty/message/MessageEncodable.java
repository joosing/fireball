package practice.netty.message;

import io.netty.buffer.ByteBuf;
import practice.netty.handler.outbound.EncodedSubMessage;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedSubMessage> encode(ByteBuf buffer);
}
