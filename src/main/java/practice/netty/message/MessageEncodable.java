package practice.netty.message;

import io.netty.buffer.ByteBuf;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedMessage> encode(ByteBuf buffer);
}
