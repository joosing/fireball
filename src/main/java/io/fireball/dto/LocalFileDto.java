package io.fireball.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
public class LocalFileDto {
    private final String file;

    public LocalFileDto(@JsonProperty("file") String file) {
        this.file = file;
    }
}
