package fontys.sem3.likeme.business.impl.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.business.interfaces.utils.FileStorageService;
import fontys.sem3.likeme.config.files.FileStorageConfig;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path fileStorageLocation;

    public FileStorageServiceImpl(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = initializeStorageLocation(fileStorageConfig.getUploadDir());
    }

    private Path initializeStorageLocation(String uploadDir) {
        try {
            Path location = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(location);
            return location;
        } catch (IOException | InvalidPathException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String prefix) {
        validateFile(file);
        String fileName = generateFileName(file, prefix);
        return saveFile(file, fileName);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }
    }

    private String generateFileName(MultipartFile file, String prefix) {
        String shortUUID = UUID.randomUUID().toString().substring(0, 4);
        String fileName = StringUtils.cleanPath(prefix + "_" + shortUUID + "_" + file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new FileStorageException("Invalid file path sequence " + fileName);
        }
        return fileName;
    }

    private String saveFile(MultipartFile file, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            }
            throw new FileNotFoundException("File not found: " + fileName);
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found " + fileName, ex);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new FileNotFoundException("File not found: " + fileName);
            }
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName, ex);
        }
    }

    @Override
    public String determineContentType(Resource resource) {
        try {
            Path filePath = Paths.get(resource.getFile().getAbsolutePath());
            return Files.probeContentType(filePath);
        } catch (Exception e) {
            return "application/octet-stream"; // Default MIME type
        }
    }
}