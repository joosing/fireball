package io.fireball.specification.message;

import io.fireball.message.ProtocolMessage;
import io.fireball.processor.InboundRequestProcessor;

/**
 * 역직렬화된 프로토콜 메시지 클래스 타입을 입력 받아 해당 메시지를 처리할 {@link InboundRequestProcessor}를 반환합니다.
 */
@FunctionalInterface
public interface InboundRequestProcessorProvider {
    InboundRequestProcessor getInboundRequestProcessor(Class<? extends ProtocolMessage> clazz);
}
