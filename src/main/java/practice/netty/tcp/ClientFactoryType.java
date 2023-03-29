package practice.netty.tcp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ClientFactoryType {
    DEFAULT(DefaultCustomClient.class, "newConnection"),
    LINE_BASED(LineBasedClient.class, "newConnection"),
    ;

    private final Class<? extends CustomClient> clientClass;
    private final String factoryMethodName;
}
