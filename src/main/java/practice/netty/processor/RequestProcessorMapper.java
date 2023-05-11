package practice.netty.processor;

@FunctionalInterface
public interface RequestProcessorMapper {
    InboundRequestProcessor getProcessor(String requestType);
}
