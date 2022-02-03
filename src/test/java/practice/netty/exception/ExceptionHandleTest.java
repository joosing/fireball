package practice.netty.exception;

import io.netty.channel.embedded.EmbeddedChannel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.netty.exception.utils.ExceptionCallable;
import practice.netty.exception.utils.ExceptionHandler;
import practice.netty.exception.utils.ExceptionThrowable;

public class ExceptionHandleTest {

    @Test
    @SneakyThrows
    @DisplayName("When you just throw exception, Then exceptionCaught() is not called")
    void WhenThrowException_thenExceptionCaughtNotCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionThrowable exceptionThrowable = new ExceptionThrowable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionThrowable,
                exceptionHandler);

        // The following assertion is valid only for EmbeddedChannel.
        // If we use Channel, then EventLoop process handler.
        // Therefore, Caller thread can not catch exception.
        Assertions.assertThrows(IllegalStateException.class, () -> channel.writeOutbound("Dummy"));
    }

    @Test
    @SneakyThrows
    @DisplayName("When you call fireExceptionCaught(), Then exceptionCaught() is called")
    void WhenCallFireExceptionCaught_thenExceptionCaughtCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionCallable exceptionCallable = new ExceptionCallable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionCallable,
                exceptionHandler);

        channel.writeOutbound("Dummy");
        Assertions.assertTrue(exceptionHandler.getCause() instanceof IllegalStateException);
    }
}
