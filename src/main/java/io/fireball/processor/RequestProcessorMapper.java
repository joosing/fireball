package io.fireball.processor;

@FunctionalInterface
public interface RequestProcessorMapper {
    InboundRequestProcessor getProcessor(String requestType);
}
