package practice.netty.study.exception;

import io.netty.channel.embedded.EmbeddedChannel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Exception Handling in Channel")
public class ExceptionHandleTest {

    @Test
    @SneakyThrows
    @DisplayName("When you throw exception from outbound, Then exceptionCaught() is not called")
    void WhenThrowExceptionFromOutbound_thenExceptionCaughtNotCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionOutboundThrowable exceptionOutboundThrowable = new ExceptionOutboundThrowable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionOutboundThrowable,
                exceptionHandler);

        // The following assertion is valid only for EmbeddedChannel.
        // If we use Channel, then EventLoop process handler.
        // Therefore, Caller thread can not catch exception.
        Assertions.assertThrows(IllegalStateException.class, () -> channel.writeOutbound("Dummy"));
        Assertions.assertNull(exceptionHandler.getCause());
    }

    @Test
    @SneakyThrows
    @DisplayName("When you throw exception from inbound, Then exceptionCaught() is called")
    void WhenThrowExceptionFromInbound_thenExceptionCaughtNotCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionInboundThrowable exceptionInboundThrowable = new ExceptionInboundThrowable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionInboundThrowable,
                exceptionHandler);

        channel.writeInbound("Dummy");
        Assertions.assertTrue(exceptionHandler.getCause() instanceof IllegalStateException);
    }

    @Test
    @SneakyThrows
    @DisplayName("When you call fireExceptionCaught() from outbound, Then exceptionCaught() is called")
    void WhenCallFireExceptionCaughtFromOutbound_thenExceptionCaughtCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionOutboundCallable exceptionOutboundCallable = new ExceptionOutboundCallable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionOutboundCallable,
                exceptionHandler);

        channel.writeOutbound("Dummy");
        Assertions.assertTrue(exceptionHandler.getCause() instanceof IllegalStateException);
    }

    @Test
    @SneakyThrows
    @DisplayName("When you call fireExceptionCaught() from inbound, Then exceptionCaught() is called")
    void WhenCallFireExceptionCaughtFromInbound_thenExceptionCaughtCalled() {
        EmbeddedChannel channel = new EmbeddedChannel();

        ExceptionInboundCallable exceptionInboundCallable = new ExceptionInboundCallable(new IllegalStateException());
        ExceptionHandler exceptionHandler = new ExceptionHandler();

        channel.pipeline().addLast(
                exceptionInboundCallable,
                exceptionHandler);

        channel.writeInbound("Dummy");
        Assertions.assertTrue(exceptionHandler.getCause() instanceof IllegalStateException);
    }
}
