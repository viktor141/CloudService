package ru.vixtor.cloudservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vixtor.cloudservice.dto.FileResponse;
import ru.vixtor.cloudservice.entity.File;
import ru.vixtor.cloudservice.sevice.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {

    private FileService service;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename, MultipartFile file) throws IOException {
        service.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        service.deleteFile(authToken, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(path = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        File file = service.downloadFile(authToken, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file.getContent());
    }

    @PutMapping("/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename,
                                          @RequestBody Map<String, String> fileNameRequest) {
        service.editFileName(authToken, filename, fileNameRequest.get("filename"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    List<FileResponse> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        return service.getAllFiles(authToken, limit).stream()
                .map(f -> new FileResponse(f.getFilename(), f.getSize()))
                .collect(Collectors.toList());
    }
}
