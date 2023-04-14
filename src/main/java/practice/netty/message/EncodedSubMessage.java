package practice.netty.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EncodedSubMessage {
    /**
     * 인코딩되어 전송될 메시지 내용을 저장합니다. contents는 ByteBuf 또는 FileRegion 타입만 네티 채널에 의해 전송될 수 있습니다.
     */
    Object message;
    /**
     * contents의 길이를 명시합니다. 만약 contents가 FileRigion 타입인 경우 최종적으로 전송될 파일의 크기를 length 필드에 저장해야 합니다.
     */
    long length;
    /*
     * 추후, 파일의 크기가 2GB를 초과하는 경우가 생기겠는가? 그러나 지금 내가 다루는 네티의 프레임 디코더에게 핸들러가 독립적으로 처리한 메시지
     * 하나를 구분하기 위한 필드이다. 따라서 int 형을 사용하는 것이 맞는 것 같다. 아니네 long 형을 받아서 전송은 int 형으로 쪼개서 하는 것이
     * 맞겠다.
     */
}
