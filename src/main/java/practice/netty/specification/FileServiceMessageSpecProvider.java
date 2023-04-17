package practice.netty.specification;

import practice.netty.message.*;
import practice.netty.processor.FileFetchRequestProcessor;
import practice.netty.processor.RequestProcessor;

public class FileServiceMessageSpecProvider implements EncodingIdProvider, MessageDecoderProvider, RequestProcessorProvider {
    private final FileServiceChannelSpecProvider channelSpec;
    private final EncodingIdManager encodingIdManager;
    private final MessageDecoderManager messageDecoderManager;
    private final RequestProcessorManager requestProcessorManager;

    public FileServiceMessageSpecProvider(FileServiceChannelSpecProvider channelSpec) {
        this.channelSpec = channelSpec;
        encodingIdManager = new EncodingIdManager();
        messageDecoderManager = new MessageDecoderManager();
        requestProcessorManager = new RequestProcessorManager();
        configEncodingIdManager();
        configMessageDecoderManager();
        configRequestProcessorManager();
    }

    private void configEncodingIdManager() {
        encodingIdManager.putId(FileFetchRequest.class, 1001);
        encodingIdManager.putId(FileFetchRxResponse.class, 2001);
        encodingIdManager.putId(FileChunkTxResponse.class, 2001);
    }
    private void configMessageDecoderManager() {
        messageDecoderManager.putDecoder(1001, FileFetchRequest::decode);
        messageDecoderManager.putDecoder(2001, FileFetchRxResponse::decode);
    }

    private void configRequestProcessorManager() {
        var fileFetchRequestProcessor = FileFetchRequestProcessor.builder()
                .chunkSize(channelSpec.fileFetch().chunkSize())
                .rootPath(channelSpec.fileFetch().rootPath())
                .build();
        requestProcessorManager.putRequestProcessor(FileFetchRequest.class, fileFetchRequestProcessor);
    }

    @Override
    public int getId(Class<? extends MessageEncodable> clazz) {
        return encodingIdManager.getId(clazz);
    }

    @Override
    public DecodeFunction getDecoder(int id) {
        return messageDecoderManager.getDecoder(id);
    }

    @Override
    public RequestProcessor getRequestProcessor(Class<? extends Message> clazz) {
        return requestProcessorManager.getRequestProcessor(clazz);
    }
}
