package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.ChunkType;
import practice.netty.message.FileChunkTxResponse;
import practice.netty.message.FileDownloadRequest;
import practice.netty.message.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Builder
public class FileFetchRequestProcessor implements RequestProcessor {
    private final int chunkSize;
    private final String rootPath;

    @Override
    public List<Message> process(Message message) {
        var fileFetchRequest = (FileDownloadRequest) message;
        var file = new File(rootPath + fileFetchRequest.getRemoteFilePath());
        var responses = new ArrayList<Message>();


        // 파일 끝을 나타내는 비어있는 청크 전송
        var startChunk = new FileChunkTxResponse(ChunkType.START_OF_FILE, 0, 0, file.getPath());
        responses.add(startChunk);

        // 파일을 청크로 분할 전송
        long start = 0;
        long remainBytes = file.length();
        while (remainBytes > 0) {
            var readBytes = (int) Math.min(remainBytes, chunkSize);
            var chunk = new FileChunkTxResponse(ChunkType.MIDDLE_OF_FILE, start, readBytes, file.getPath());
            responses.add(chunk);
            remainBytes -= readBytes;
            start += readBytes;
        }

        // 파일 끝을 나타내는 비어있는 청크 전송
        var lastChunk = new FileChunkTxResponse(ChunkType.END_OF_FILE, start, 0, file.getPath());
        responses.add(lastChunk);
        return responses;
    }
}
