package practice.netty.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventLoopGroupConfig {
    @Bean
    public EventLoopGroup clientEventLoopGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public EventLoopGroup fileStoreEventLoopGroup() {
        return new NioEventLoopGroup();
    }
}
