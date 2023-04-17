package practice.netty.specification;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("ALL")
@Accessors(fluent = true)
@Getter
public class FileServiceChannelSpecProvider {
    private final FileFetchSpec fileFetch = new FileFetchSpec();
    private final HeaderSpec header = new HeaderSpec();

    @Getter
    public static class FileFetchSpec {
        private final int chunkSize = 1024 * 1024 * 5;
        private final String rootPath = "./";
    }

    @Getter
    public static class HeaderSpec {
        private final IdSpec id = new IdSpec();
        private final LengthSpec length = new LengthSpec();

        @Getter
        // 아이디 필드 스펙
        public static class IdSpec {
            private final int length = 4;
            public int read(ByteBuf buf) {
                return buf.readInt();
            }
            public void write(ByteBuf buf, int value) {
                buf.writeInt(value);
            }
        }

        @Getter
        // 길이 필드 스펙
        public static class LengthSpec {
            private final int length = 4;
            public int read(ByteBuf buf) {
                return buf.readInt();
            }
            public void write(ByteBuf buf, int value) {
                buf.writeInt(value);
            }
        }
    }
}
