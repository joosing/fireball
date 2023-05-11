package practice.netty.message;

import io.netty.buffer.ByteBuf;
import practice.netty.handler.outbound.EncodedPartialContents;

import java.util.List;

@FunctionalInterface
public interface MessageEncodable {
    List<EncodedPartialContents> encode(ByteBuf buffer);
}
