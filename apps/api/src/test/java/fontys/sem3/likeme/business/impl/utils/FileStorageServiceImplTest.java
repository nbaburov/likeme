package fontys.sem3.likeme.business.impl.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import fontys.sem3.likeme.business.exception.utils.file.FileNotFoundException;
import fontys.sem3.likeme.business.exception.utils.file.FileStorageException;
import fontys.sem3.likeme.config.files.FileStorageConfig;

class FileStorageServiceImplTest {
    private FileStorageServiceImpl fileStorageService;
    private FileStorageConfig fileStorageConfig;

    @TempDir
    Path tempDir;

    private static final String TEST_PREFIX = "test";
    private static final String TEST_FILENAME = "test.txt";
    private static final String TEST_CONTENT = "test content";
    private static final String INVALID_FILENAME = "../test.txt";

    @BeforeEach
    void setUp() {
        fileStorageConfig = new FileStorageConfig();
        fileStorageConfig.setUploadDir(tempDir.toString());
        fileStorageService = new FileStorageServiceImpl(fileStorageConfig);
    }

    @Test
    void storeFile_Success() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                TEST_FILENAME,
                TEST_FILENAME,
                "text/plain",
                TEST_CONTENT.getBytes());

        // Act
        String storedFileName = fileStorageService.storeFile(file, TEST_PREFIX);

        // Assert
        assertTrue(storedFileName.startsWith(TEST_PREFIX));
        assertTrue(storedFileName.endsWith(TEST_FILENAME));
        assertTrue(Files.exists(tempDir.resolve(storedFileName)));
    }

    @Test
    void storeFile_EmptyFile_ThrowsException() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                TEST_FILENAME,
                TEST_FILENAME,
                "text/plain",
                new byte[0]);

        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> fileStorageService.storeFile(emptyFile, TEST_PREFIX));

        assertEquals("Failed to store empty file", exception.getMessage());
    }

    @Test
    void storeFile_NullFile_ThrowsException() {
        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> fileStorageService.storeFile(null, TEST_PREFIX));

        assertEquals("Failed to store empty file", exception.getMessage());
    }

    @Test
    void storeFile_InvalidPath_ThrowsException() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                INVALID_FILENAME,
                INVALID_FILENAME,
                "text/plain",
                TEST_CONTENT.getBytes());

        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> fileStorageService.storeFile(file, TEST_PREFIX));

        assertTrue(exception.getMessage().contains("Invalid file path sequence"));
    }

    @Test
    void loadFileAsResource_Success() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve(TEST_FILENAME);
        Files.write(testFile, TEST_CONTENT.getBytes());

        // Act
        Resource resource = fileStorageService.loadFileAsResource(TEST_FILENAME);

        // Assert
        assertTrue(resource.exists());
        assertEquals(TEST_CONTENT, new String(Files.readAllBytes(Paths.get(resource.getURI()))));
    }

    @Test
    void loadFileAsResource_FileNotFound_ThrowsException() {
        // Act & Assert
        FileNotFoundException exception = assertThrows(
                FileNotFoundException.class,
                () -> fileStorageService.loadFileAsResource("nonexistent.txt"));

        assertEquals("File not found: nonexistent.txt", exception.getMessage());
    }

    @Test
    void deleteFile_Success() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve(TEST_FILENAME);
        Files.write(testFile, TEST_CONTENT.getBytes());

        // Act
        assertDoesNotThrow(() -> fileStorageService.deleteFile(TEST_FILENAME));

        // Assert
        assertFalse(Files.exists(testFile));
    }

    @Test
    void deleteFile_FileNotFound_ThrowsException() {
        // Act & Assert
        FileNotFoundException exception = assertThrows(
                FileNotFoundException.class,
                () -> fileStorageService.deleteFile("nonexistent.txt"));

        assertEquals("File not found: nonexistent.txt", exception.getMessage());
    }

    @Test
    void determineContentType_Success() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve(TEST_FILENAME);
        Files.write(testFile, TEST_CONTENT.getBytes());
        Resource resource = fileStorageService.loadFileAsResource(TEST_FILENAME);

        // Act
        String contentType = fileStorageService.determineContentType(resource);

        // Assert
        assertNotNull(contentType);
        assertTrue(contentType.equals("text/plain") || contentType.equals("application/octet-stream"));
    }

    @Test
    void initializeStorageLocation_Success() throws IOException {
        // Arrange
        Path newTempDir = tempDir.resolve("newDir");
        FileStorageConfig newConfig = new FileStorageConfig();
        newConfig.setUploadDir(newTempDir.toString());

        // Act
        FileStorageServiceImpl newService = new FileStorageServiceImpl(newConfig);

        // Assert
        assertTrue(Files.exists(newTempDir));
        assertTrue(Files.isDirectory(newTempDir));
    }

    @Test
    void initializeStorageLocation_InvalidPath_ThrowsException() {
        // Arrange
        FileStorageConfig invalidConfig = new FileStorageConfig();
        invalidConfig.setUploadDir("/\0invalid/path"); // Invalid character in path

        // Act & Assert
        FileStorageException exception = assertThrows(
                FileStorageException.class,
                () -> new FileStorageServiceImpl(invalidConfig));
        
        assertEquals("Could not create the directory where the uploaded files will be stored", 
                exception.getMessage());
        assertTrue(exception.getCause() instanceof InvalidPathException);
    }
}