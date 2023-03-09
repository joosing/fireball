package practice.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

@DisplayName("DelimiterBasedFrameDecoder")
public class DelimiterBasedFrameDecoderTest {

    @Test
    void Test() {
        // Given :
        ByteBuf delimiter1 = Unpooled.buffer();
        ByteBuf delimiter2 = Unpooled.buffer();
        ByteBuf readSample = Unpooled.buffer();

        delimiter1.writeCharSequence("word1", StandardCharsets.UTF_8);
        delimiter2.writeCharSequence("word2", StandardCharsets.UTF_8);
        readSample.writeCharSequence("this is my word1this is my second word2", StandardCharsets.UTF_8);

        EmbeddedChannel channel = new EmbeddedChannel(
                new DelimiterBasedFrameDecoder(1024, delimiter1, delimiter2),
                new StringDecoder()
        );

        // When
        Assertions.assertTrue(channel.writeInbound(readSample));

        // Then
        Assertions.assertEquals("this is my ", channel.readInbound());
        Assertions.assertEquals("this is my second ", channel.readInbound());
    }
}
