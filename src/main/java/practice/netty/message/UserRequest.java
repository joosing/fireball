package practice.netty.message;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;

@Getter
@Accessors(fluent = true)
public abstract class UserRequest {
    private final CompletableFuture<Void> responseFuture;

    protected UserRequest() {
        responseFuture = new CompletableFuture<>();
    }
}
