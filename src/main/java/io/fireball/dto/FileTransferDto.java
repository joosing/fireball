package io.fireball.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class FileTransferDto {
    private final LocalFile local;
    private final RemoteFile remote;

    @Getter
    @ToString
    public static class LocalFile {
        private final String filePath;
        /**
         * 멤버가 하나인 생성자를 가진 경우 매핑 아래와 같이 매핑 필드를 명시해야 Jackson 라이브러리에 Deserialize 동작이 정상 수행됩니다.
         * 다음 이슈를 참조하세요. (https://github.com/FasterXML/jackson-databind/issues/1498)
         */
        @JsonCreator
        public LocalFile(@JsonProperty("filePath") String filePath) {
            this.filePath = filePath;
        }
    }
    @Getter
    @RequiredArgsConstructor
    @ToString
    public static class RemoteFile {
        private final String ip;
        private final int port;
        private final String filePath;
    }
}
