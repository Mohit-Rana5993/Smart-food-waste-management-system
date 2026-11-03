
package com.smartfood.wastemanagement.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class UploadController {

    private final Path root;

    public UploadController(
            @Value("${app.upload-dir:uploads}") String dir) {
        root = Path.of(dir).toAbsolutePath().normalize();
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> file(
            @PathVariable String filename) {

        try {
            Path p = root.resolve(filename).normalize();

            if (!p.startsWith(root) || !Files.isRegularFile(p)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(p.toUri());

            String type = Files.probeContentType(p);

            MediaType media = type != null && type.startsWith("image/")
                    ? MediaType.parseMediaType(type)
                    : MediaType.APPLICATION_OCTET_STREAM;

            String safeFilename = filename.replace("\"", "");

            return ResponseEntity.ok()
                    .contentType(media)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + safeFilename + "\""
                    )
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
