package fontys.sem3.likeme.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.controller.dto.file.FileUploadResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Controller", description = "Endpoints for file management")
public class FileController {
        private final FileStorageService fileStorageService;

        @Operation(summary = "Upload file")
        @PostMapping
        public ResponseEntity<FileUploadResponseDTO> uploadFile(
                        @RequestParam("file") MultipartFile file,
                        @RequestParam("prefix") String prefix) {
                String fileName = fileStorageService.storeFile(file, prefix);
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/files/")
                                .path(fileName)
                                .toUriString();

                return ResponseEntity.ok(new FileUploadResponseDTO(
                                fileName,
                                fileDownloadUri,
                                file.getContentType(),
                                file.getSize()));
        }

        @Operation(summary = "Get a file")
        @GetMapping("/{fileName:.+}")
        public ResponseEntity<Resource> getFile(
                        @PathVariable String fileName,
                        HttpServletRequest request) {
                Resource resource = fileStorageService.loadFileAsResource(fileName);
                String contentType = fileStorageService.determineContentType(resource);

                return ResponseEntity.ok()
                                .contentType(MediaType.parseMediaType(contentType))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + resource.getFilename() + "\"")
                                .body(resource);
        }

        @Operation(summary = "Delete a file")
        @RolesAllowed("ADMIN")
        @DeleteMapping("/{fileName:.+}")
        public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
                fileStorageService.deleteFile(fileName);
                return ResponseEntity.noContent().build();
        }
}