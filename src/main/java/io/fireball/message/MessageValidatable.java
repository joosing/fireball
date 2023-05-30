package io.fireball.message;

public interface MessageValidatable {
    default void validate() throws Exception{
        // Empty implementation
    }
}
