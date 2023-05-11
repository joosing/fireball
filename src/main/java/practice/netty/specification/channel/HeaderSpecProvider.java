package practice.netty.specification.channel;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("FieldMayBeStatic")
public class HeaderSpecProvider {
    private final IdSpec id = new IdSpec();
    private final LengthSpec length = new LengthSpec();

    @Getter
    // 아이디 필드 스펙
    public static class IdSpec {
        private final int length = 4;
        public int readFunc(ByteBuf buf) {
            return buf.readInt();
        }
        public void writeFunc(ByteBuf buf, int value) {
            buf.writeInt(value);
        }
    }

    @Getter
    // 길이 필드 스펙
    public static class LengthSpec {
        private final int length = 4;
        public int readFunc(ByteBuf buf) {
            return buf.readInt();
        }
        public void writeFunc(ByteBuf buf, int value) {
            buf.writeInt(value);
        }
    }
}
