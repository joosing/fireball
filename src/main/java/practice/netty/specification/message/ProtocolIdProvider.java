package practice.netty.specification.message;

import practice.netty.message.MessageEncodable;

/**
 * 인코딩될 수 있는 메시지 클래스가 직렬화될 때 사용될 메시지 ID를 제공합니다.
 */
@FunctionalInterface
public interface ProtocolIdProvider {
    int getProtocolId(Class<? extends MessageEncodable> clazz);
}
