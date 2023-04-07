package practice.netty.specification;

import practice.netty.message.FileFetchRegionResponse;
import practice.netty.message.FileFetchRequest;
import practice.netty.message.FileFetchResponse;

public class FileServiceMessageSpecProvider extends MessageSpecProvider {

    @Override
    protected void configClassToIdMap() {
        classToIdMap.put(FileFetchRequest.class, 1001);
        classToIdMap.put(FileFetchResponse.class, 2001);
        classToIdMap.put(FileFetchRegionResponse.class, 2001);
    }

    @Override
    protected void configIdToDecoderMap() {
        idToDecoderMap.put(1001, FileFetchRequest::decode);
        idToDecoderMap.put(2001, FileFetchResponse::decode);
    }
}
