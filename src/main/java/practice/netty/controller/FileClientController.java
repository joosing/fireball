package practice.netty.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;
import practice.netty.service.FileClientService;

import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileClientController {
    private final FileClientService fileClientService;

    @PostMapping("/local/{filePath}")
    public ResponseEntity<Void> downloadFile(@PathVariable String filePath,
                                             @RequestBody RemoteFile remoteFile) throws ExecutionException,
            InterruptedException {
//        log.info("filePath: {}", filePath);
//        log.info("remoteFileDto: {}", remoteFile);
        fileClientService.downloadFile(remoteFile, new LocalFile(filePath));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remote/{ip}/{port}/{filePath}")
    public ResponseEntity<Void> uploadFile(@PathVariable String ip,
                                           @PathVariable int port,
                                           @PathVariable String filePath,
                                           @RequestBody LocalFile localFile) {
//        log.info("ip: {}", ip);
//        log.info("port: {}", port);
//        log.info("filePath: {}", filePath);
//        log.info("localFileDto: {}", localFile);
        fileClientService.uploadFile(localFile, new RemoteFile(ip, port, filePath));
        return ResponseEntity.ok().build();
    }
}
