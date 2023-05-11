package practice.netty.specification.message;

import practice.netty.message.ProtocolMessage;
import practice.netty.processor.InboundRequestProcessor;

/**
 * 역직렬화된 프로토콜 메시지 클래스 타입을 입력 받아 해당 메시지를 처리할 {@link InboundRequestProcessor}를 반환합니다.
 */
@FunctionalInterface
public interface InboundRequestProcessorProvider {
    InboundRequestProcessor getInboundRequestProcessor(Class<? extends ProtocolMessage> clazz);
}
