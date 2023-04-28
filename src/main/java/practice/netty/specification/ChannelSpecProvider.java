package practice.netty.specification;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

@Component
@Accessors(fluent = true)
@Getter
public class ChannelSpecProvider {
    private final FileHandleSpecProvider fileHandleSpec = new FileHandleSpecProvider();
    private final HeaderSpecProvider headerSpec = new HeaderSpecProvider();
}
