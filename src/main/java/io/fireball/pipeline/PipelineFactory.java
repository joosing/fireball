package io.fireball.pipeline;

import java.util.List;

@FunctionalInterface
public interface PipelineFactory {
    List<HandlerFactory> get();
}
