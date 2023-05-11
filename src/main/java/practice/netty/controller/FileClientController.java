package practice.netty.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import practice.netty.dto.FileTransferDto;
import practice.netty.service.FileClientService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileClientController {
    private final FileClientService fileClientService;

    @PostMapping("/download")
    public ResponseEntity<Void> downloadFile(@RequestBody FileTransferDto fileTransferDto) throws Exception {
        fileClientService.downloadFile(fileTransferDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestBody FileTransferDto fileTransferDto) throws Exception {
        fileClientService.uploadFile(fileTransferDto);
        return ResponseEntity.ok().build();
    }
}
