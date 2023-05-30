package io.fireball.service;

import java.util.concurrent.ExecutionException;

@FunctionalInterface
public interface FilerServer {
    void start(int bindPort) throws InterruptedException, ExecutionException;
}
