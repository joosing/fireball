package practice.netty.processor;

import practice.netty.message.ChunkType;
import practice.netty.message.OutboundFileChunk;
import practice.netty.message.ProtocolMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileChunkTransferProcessor implements FileTransferProcessor {

    @Override
    public List<ProtocolMessage> process(String srcPath, String dstPath, int chunkSize) {
        var srcFile = new File(srcPath);
        var result = new ArrayList<ProtocolMessage>();

        // 파일의 시작을 나타내는 비어있는 청크 전송
        var startChunk = new OutboundFileChunk(ChunkType.START_OF_FILE, srcPath, dstPath, 0, 0);
        result.add(startChunk);

        // 파일을 청크로 분할 전송
        long start = 0;
        long remainBytes = srcFile.length();
        while (remainBytes > 0) {
            var readBytes = (int) Math.min(remainBytes, chunkSize);
            var chunk = new OutboundFileChunk(ChunkType.FILE_CONTENTS, srcPath, dstPath, start, readBytes);
            result.add(chunk);
            remainBytes -= readBytes;
            start += readBytes;
        }

        // 파일 끝을 나타내는 비어있는 청크 전송
        var lastChunk = new OutboundFileChunk(ChunkType.END_OF_FILE, srcPath, dstPath, start, 0);
        result.add(lastChunk);

        return result;
    }
}
