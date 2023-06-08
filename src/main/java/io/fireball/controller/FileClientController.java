package io.fireball.controller;

import io.fireball.dto.FileDownloadDto;
import io.fireball.dto.FileUploadDto;
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
    public ResponseEntity<Void> downloadFileNew(@RequestBody FileDownloadDto spec) throws Exception {
        fileClient.downloadFile(spec);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestBody FileUploadDto spec) throws Exception {
        fileClient.uploadFile(spec);
        return ResponseEntity.ok().build();
    }
}
