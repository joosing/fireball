package io.fireball.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@Slf4j
@Profile("test")
@RequestMapping("/test/multipart")
public class MultipartFileController {

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        // check if file is empty
        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // save the file on the local file system
        Path path = Paths.get("./files/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // return success response
        return ResponseEntity.ok().build();
    }
}