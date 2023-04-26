package practice.netty.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/file")
public class FileServerController {

    @PutMapping("/fetch/{ip}/{port}/{filePath}")
    public ResponseEntity<Void> fetchFile(@PathVariable String ip, @PathVariable int port, @PathVariable String filePath) {
        log.info("filePath: {}", filePath);
        return ResponseEntity.ok().build();
    }
}
