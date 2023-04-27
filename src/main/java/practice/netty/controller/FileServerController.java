package practice.netty.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import practice.netty.dto.LocalFileDto;
import practice.netty.dto.RemoteFileDto;

@Slf4j
@Controller
@RequestMapping("/files")
public class FileServerController {

    @PostMapping("/local/{filePath}")
    public ResponseEntity<Void> downloadFile(@PathVariable String filePath,
                                             @RequestBody RemoteFileDto remoteFileDto) {
        log.info("filePath: {}", filePath);
        log.info("remoteFileDto: {}", remoteFileDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/remote/{ip}/{port}/{filePath}")
    public ResponseEntity<Void> uploadFile(@PathVariable String ip,
                                           @PathVariable int port,
                                           @PathVariable String filePath,
                                           @RequestBody LocalFileDto localFileDto) {
        log.info("ip: {}", ip);
        log.info("port: {}", port);
        log.info("filePath: {}", filePath);
        log.info("localFileDto: {}", localFileDto);
        return ResponseEntity.ok().build();
    }
}
