package fontys.sem3.likeme.controller.dto.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponseDTO {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}