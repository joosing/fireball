package practice.netty.processor;

@FunctionalInterface
public interface RequestProcessorMapper {
    RxRequestProcessor getProcessor(String requestType);
}
