package practice.netty.specification.message;

import practice.netty.message.DecodeFunction;

/**
 * 디코딩 될 수 있는 메시지의 프로토콜 ID를 입력받아 디코딩 함수 객체를 반환합니다.
 */
@FunctionalInterface
public interface MessageDecoderProvider {
    DecodeFunction getDecoder(int id);
}
