package com.vwv.quadtree.controller;

import com.vwv.quadtree.model.QuadTreeNode;
import com.vwv.quadtree.service.QuadTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class QuadTreeController {

    @Autowired
    private QuadTreeService quadTreeService;

    private final String filePath = "src/main/resources/static/output/";


    @PostMapping(value = "/compress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public QuadTreeNode compress(@RequestParam("image") MultipartFile imageFile) throws IOException {
        File tempFile = File.createTempFile("upload-", imageFile.getOriginalFilename());
        imageFile.transferTo(tempFile);
        try {
            return quadTreeService.compress(tempFile);
        } finally {
            tempFile.delete();
        }
    }

    @PostMapping(value = "/decompress", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<FileSystemResource> decompress(
            @RequestBody QuadTreeNode root,
            @RequestParam int width,
            @RequestParam int height) throws IOException {

        String fileName = "decompressed-" + UUID.randomUUID() + ".png";
        String outputPath = filePath + fileName;

        File outputFile = quadTreeService.decompress(root, width, height, outputPath);
        FileSystemResource resource = new FileSystemResource(outputFile);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + outputFile.getName())
                .body(resource);
    }
}