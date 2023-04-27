package practice.netty.dto;

import lombok.Value;

@Value
public class RemoteFileDto {
    String ip;
    int port;
    String filePath;
}
