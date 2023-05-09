package practice.netty.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.netty.dto.LocalFile;
import practice.netty.dto.RemoteFile;
import practice.netty.service.FileClientService;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileClientController {
    private final FileClientService fileClientService;

    @PostMapping("/local/{filePath}")
    public ResponseEntity<Void> downloadFile(@PathVariable String filePath,
                                             @RequestBody RemoteFile remoteFile) throws Exception {
        fileClientService.downloadFile(remoteFile, new LocalFile(filePath));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remote/{ip}/{port}/{filePath}")
    public ResponseEntity<Void> uploadFile(@PathVariable String ip,
                                           @PathVariable int port,
                                           @PathVariable String filePath,
                                           @RequestBody LocalFile localFile) throws Exception {
        fileClientService.uploadFile(localFile, new RemoteFile(ip, port, filePath));
        return ResponseEntity.ok().build();
    }
}
