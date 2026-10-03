package fontys.sem3.likeme.business.interfaces.utils;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String prefix);

    Resource loadFileAsResource(String fileName);

    void deleteFile(String fileName);

    String determineContentType(Resource resource);
}