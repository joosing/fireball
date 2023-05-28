package practice.netty.pipeline;

import practice.netty.common.HandlerFactory;

import java.util.List;

@FunctionalInterface
public interface PipelineFactory {
    List<HandlerFactory> get();
}
