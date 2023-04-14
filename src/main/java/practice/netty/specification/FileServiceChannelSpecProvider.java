package practice.netty.specification;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@SuppressWarnings("ALL")
public class FileServiceChannelSpecProvider {
    private final FileFetchSpec fileFetch = new FileFetchSpec();
    private final HeaderSpec header = new HeaderSpec();

    public HeaderSpec header() {
        return header;
    }

    public FileFetchSpec fileFetch() {
        return fileFetch;
    }

    @Getter
    public static class FileFetchSpec {
        private final int chunkSize = 1024 * 1024 * 5;
        private final String rootPath = "./";
    }

    /**
     * 네티 채널에서 하나의 단위로 수신할 수 있는 메시지 헤더를 뜻합니다. 네티에서 제공하는 LengthBasedFrameDecoder는 메시지 최대 크기를
     * int 형 최대값으로 제한하고 있습니다. 따라서 메시지의 길이도 int 형 최대값 보다 클 수 없습니다.
     */
    public static class HeaderSpec {
        private final IdSpec id = new IdSpec();
        private final LengthSpec length = new LengthSpec();
        public IdSpec id() {
            return id;
        }
        public LengthSpec length() {
            return length;
        }
        // 아이디 필드 스펙
        public static class IdSpec {
            private static final int length = 4;
            public int length() {
                return length;
            }
            public int read(ByteBuf buf) {
                return buf.readInt();
            }
            public void write(ByteBuf buf, int value) {
                buf.writeInt(value);
            }
        }
        // 길이 필드 스펙
        public static class LengthSpec {
            private static final int length = 4;
            public int length() {
                return length;
            }
            public int read(ByteBuf buf) {
                return buf.readInt();
            }
            public void write(ByteBuf buf, int value) {
                buf.writeInt(value);
            }
        }
    }
}
