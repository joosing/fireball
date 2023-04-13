package practice.netty.util;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@UtilityClass
public class PrintByteBufUtil {
    // ByteBuf의 내용을 바이트 배열로 변환하여 출력합니다.
    public void printByteArray(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        log.info(Arrays.toString(bytes));
    }
}
