package practice.netty.processor;

import lombok.Builder;
import practice.netty.message.ChunkType;
import practice.netty.message.FileRxChunk;
import practice.netty.message.ProtocolMessage;
import practice.netty.message.ResponseMessage;
import practice.netty.specification.ResponseCode;

import java.util.List;

@Builder
public class FileUploadRxRequestProcessor implements RxRequestProcessor {

    @Override
    public List<ProtocolMessage> process(ProtocolMessage message) {
        var rxChunk = (FileRxChunk) message;
        assert rxChunk.chunkType() == ChunkType.END_OF_FILE;
        return List.of(new ResponseMessage(ResponseCode.OK));
    }
}
