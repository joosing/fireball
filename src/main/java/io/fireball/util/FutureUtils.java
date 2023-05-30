package io.fireball.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@UtilityClass
public class FutureUtils {
    public static void get(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
