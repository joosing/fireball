package io.fireball.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum ChunkType {
    START_OF_FILE(1),
    MIDDLE_OF_FILE(2),
    END_OF_FILE(3);
    private final int value;

    public static ChunkType of(int value) {
        return switch (value) {
            case 1 -> START_OF_FILE;
            case 2 -> MIDDLE_OF_FILE;
            case 3 -> END_OF_FILE;
            default -> throw new IllegalArgumentException("Unknown chunk type: " + value);
        };
    }
}
