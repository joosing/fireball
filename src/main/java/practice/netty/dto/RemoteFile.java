package practice.netty.dto;

import lombok.Value;

@Value
public class RemoteFile {
    String ip;
    int port;
    String path;
}
