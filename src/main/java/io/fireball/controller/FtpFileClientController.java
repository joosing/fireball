package io.fireball.controller;

import io.fireball.dto.FileTransferDto;
import io.fireball.specification.channel.FileClientSpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Profile("test")
@RestController
@RequestMapping("/test/ftp")
@RequiredArgsConstructor
public class FtpFileClientController {
    private final FileClientSpec fileClientSpec;

    @PostMapping("/upload")
    public ResponseEntity<Boolean> uploadFile(@RequestBody FileTransferDto fileTransferDto) throws Exception {
        // create
        var ftp = new FTPClient();

        // connect
        ftp.connect(fileTransferDto.getRemoteIp(), fileTransferDto.getRemotePort());
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        // login
        ftp.login("test", "1234");

        // normalize path
        var srcPath = Path.of(fileClientSpec.rootPath(), fileTransferDto.getLocalFile()).normalize().toString();

        // upload file
        var result = ftp.storeFile(fileTransferDto.getRemoteFile(), new FileInputStream(srcPath));
        return ResponseEntity.ok(result);
    }
}
