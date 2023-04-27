package practice.netty.dto;

import lombok.*;

/**
 * 필드가 하나인 DTO에 @Value 어노테이션을 사용하면 Jackson 라이브러리에 의해 Deserialize 동작(JSON to Class)이 정상 수행되지 않습니다.
 * 원인은 필드가 하나인 생성자는 Jackson에서 특별한 용도(delegating-creators)로 사용하기 때문입니다. 자세한 내용은 다음 이슈를 참고하세요. <br>
 * - https://github.com/FasterXML/jackson-databind/issues/1498 <br>
 * - https://stackoverflow.com/questions/41243608/jackson-single-argument-constructor-with-single-argument-fails-with-parameternam <br>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LocalFileDto {
    String filePath;
}
