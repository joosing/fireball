package practice.netty.specification;

import practice.netty.message.DecodeFunction;

@FunctionalInterface
public interface MessageDecoderProvider {
    DecodeFunction getDecoder(int id);
}
