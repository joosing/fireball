package practice.netty.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * 필드가 하나인 DTO에 @Value 어노테이션을 사용하면 Jackson 라이브러리에 의해 Deserialize 동작(JSON to Class)이 정상 수행되지 않습니다.
 * 따라서 명시적으로 Deserialize에 사용될 생성자를 지정해 주어야 합니다. 자세한 내용은 다음 이슈를 참고하세요. <br>
 * - https://github.com/FasterXML/jackson-databind/issues/1498 <br>
 * - https://stackoverflow.com/questions/41243608/jackson-single-argument-constructor-with-single-argument-fails-with-parameternam <br>
 */
@Value
public class LocalFile {
    String path;
    @JsonCreator
    public LocalFile(@JsonProperty("path") String path) {
        this.path = path;
    }
}
