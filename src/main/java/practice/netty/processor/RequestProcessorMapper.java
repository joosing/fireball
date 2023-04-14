package practice.netty.processor;

@FunctionalInterface
public interface RequestProcessorMapper {
    RequestProcessor getProcessor(String requestType);
}
