package practice.netty.processor;

import practice.netty.message.Message;

import java.util.List;


@FunctionalInterface
public interface RequestProcessor {
    List<Message> process(Message message);
}
