package practice.netty.pipeline;

import practice.netty.common.HandlerWorkerPair;

import java.util.List;

@FunctionalInterface
public interface PipelineManager {
    List<HandlerWorkerPair> getPipeline();
}
