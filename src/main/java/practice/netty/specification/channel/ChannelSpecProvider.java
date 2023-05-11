package practice.netty.specification.channel;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@Accessors(fluent = true)
@Getter
public class ChannelSpecProvider {
    private final FileServerSpec server = new FileServerSpec();
    private final FileClientSpec client = new FileClientSpec();
    private final HeaderSpecProvider header = new HeaderSpecProvider();
}
