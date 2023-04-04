package practice.netty.helper;

import lombok.Builder;
import lombok.Value;
import practice.netty.tcp.client.CustomClientType;
import practice.netty.tcp.server.CustomServerType;

@Builder
@Value
public class TcpLoopbackTestSetting {
    public int serverPort;
    public int nClient;
    public CustomServerType serverType;
    public CustomClientType clientType;
}
