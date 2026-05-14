package com.mahasbr.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.text.Normalizer;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.config.FileStorageProperties;
import com.mahasbr.scanner.VirusScanner;
import com.mahasbr.validators.FileValidator;

@Service
public class FileStorageService {

    private static final String DEFAULT_SEGMENT = "general";
    private static final Set<PosixFilePermission> DIRECTORY_PERMISSIONS = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE);
    private static final Set<PosixFilePermission> FILE_PERMISSIONS = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE);

    private final Path uploadDir;
    private final FileValidator validator;
    private final VirusScanner scanner;

    public FileStorageService(FileStorageProperties props, FileValidator validator, VirusScanner scanner) {
        if (props.getUploadDir() == null || props.getUploadDir().isBlank()) {
            throw new IllegalStateException("Upload directory is not configured. Set app.file.upload-dir or FILE_UPLOAD_DIR.");
        }

        this.uploadDir = Paths.get(props.getUploadDir()).toAbsolutePath().normalize();
        this.validator = validator;
        this.scanner = scanner;

        try {
            Files.createDirectories(uploadDir);
            secureDirectoryIfPossible(uploadDir);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Unable to create upload directory '" + uploadDir
                            + "'. Ensure the container user has write permission to this path.",
                    ex);
        }
    }

    public String store(MultipartFile file, String area, String activity) {
        return store(file, area, activity, null);
    }

    public String store(MultipartFile file, String area, String activity, FileUploadPolicy policy) {
        validator.validate(file, policy);

        Path targetDirectory = createTargetDirectory(area, activity);
        Path target = targetDirectory.resolve(buildStoredFileName(file.getOriginalFilename()));
        Path temporaryFile = targetDirectory.resolve("." + UUID.randomUUID() + ".uploading");

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, temporaryFile, StandardCopyOption.REPLACE_EXISTING);
            secureFileIfPossible(temporaryFile);

            if (!scanner.isClean(temporaryFile)) {
                Files.deleteIfExists(temporaryFile);
                throw new SecurityException("File is infected");
            }

            moveAtomicallyIfPossible(temporaryFile, target);
            secureFileIfPossible(target);
            return toRelativePath(target);
        } catch (Exception ex) {
            deleteIfExists(temporaryFile);
            deleteIfExists(target);
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    public String move(String currentRelativePath, String area, String activity) {
        Path source = resolve(currentRelativePath);
        if (!Files.exists(source)) {
            return currentRelativePath;
        }

        Path targetDirectory = createTargetDirectory(area, activity);
        Path target = targetDirectory.resolve(source.getFileName());

        try {
            if (Files.exists(target)) {
                target = targetDirectory.resolve(UUID.randomUUID() + "-" + source.getFileName());
            }
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return toRelativePath(target);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to move file to requested activity folder", ex);
        }
    }

    public Resource loadAsResource(String relativePath) throws FileNotFoundException {
        Path filePath = resolve(relativePath);
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath) || !Files.isReadable(filePath)) {
            throw new FileNotFoundException("File not found: " + relativePath);
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Invalid file URL for path: " + relativePath, ex);
        }
    }

    public void deleteIfExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        deleteIfExists(resolve(relativePath));
    }

    public Path resolve(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new IllegalArgumentException("Relative path cannot be null or blank");
        }

        Path resolvedPath = uploadDir.resolve(relativePath).normalize();
        if (!resolvedPath.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid relative path");
        }
        return resolvedPath;
    }

    private Path createTargetDirectory(String area, String activity) {
        String safeArea = sanitizePathSegment(area);
        String safeActivity = sanitizePathSegment(activity);
        Path targetDirectory = uploadDir.resolve(Paths.get(safeArea, safeActivity)).normalize();

        if (!targetDirectory.startsWith(uploadDir)) {
            throw new IllegalArgumentException("Invalid upload target");
        }

        try {
            Files.createDirectories(targetDirectory);
            secureDirectoryIfPossible(targetDirectory);
            return targetDirectory;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create upload directory '" + targetDirectory + "'", ex);
        }
    }

    private String buildStoredFileName(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        if (StringUtils.hasText(extension)) {
            return UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
        }
        return UUID.randomUUID().toString();
    }

    private String sanitizePathSegment(String segment) {
        if (!StringUtils.hasText(segment)) {
            return DEFAULT_SEGMENT;
        }

        String normalized = Normalizer.normalize(segment, Normalizer.Form.NFKC)
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        if (!StringUtils.hasText(normalized)) {
            return DEFAULT_SEGMENT;
        }

        return normalized;
    }

    private String toRelativePath(Path filePath) {
        return uploadDir.relativize(filePath).toString().replace('\\', '/');
    }

    private void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to clean up file: " + path, ex);
        }
    }

    private void moveAtomicallyIfPossible(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void secureDirectoryIfPossible(Path directory) {
        if (!supportsPosix(directory)) {
            return;
        }

        try {
            Files.setPosixFilePermissions(directory, DIRECTORY_PERMISSIONS);
        } catch (IOException ignored) {
            // Best-effort hardening on supported filesystems.
        }
    }

    private void secureFileIfPossible(Path file) {
        if (!supportsPosix(file)) {
            return;
        }

        try {
            Files.setPosixFilePermissions(file, FILE_PERMISSIONS);
        } catch (IOException ignored) {
            // Best-effort hardening on supported filesystems.
        }
    }

    private boolean supportsPosix(Path path) {
        try {
            return Files.getFileStore(path).supportsFileAttributeView("posix");
        } catch (IOException ex) {
            return false;
        }
    }
}
