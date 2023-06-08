package io.fireball.controller;

import io.fireball.dto.FileTransferDto;
import io.fireball.service.FileClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileClientController {
    private final FileClient fileClient;

    @PostMapping("/download")
    public ResponseEntity<Void> downloadFile(@RequestBody FileTransferDto fileTransferDto) throws Exception {
        fileClient.downloadFile(fileTransferDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestBody FileTransferDto fileTransferDto) throws Exception {
        fileClient.uploadFile(fileTransferDto);
        return ResponseEntity.ok().build();
    }
}
