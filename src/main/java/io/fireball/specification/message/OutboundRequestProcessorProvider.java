package io.fireball.specification.message;


import io.fireball.message.UserRequest;
import io.fireball.processor.OutboundRequestProcessor;

/**
 * 사용자의 요청 메시지 클래스 타입을 입력 받아 해당 메시지를 처리할 {@link OutboundRequestProcessor}를 반환합니다.
 */
@FunctionalInterface
public interface OutboundRequestProcessorProvider {
    OutboundRequestProcessor getOutboundRequestProcessor(Class<? extends UserRequest> clazz);
}
