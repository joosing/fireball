package practice.netty.specification.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor
public class ChannelSpecProvider {
    private final FileServerSpec server;
    private final FileClientSpec client;
    private final HeaderSpecProvider header;
}
