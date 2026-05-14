package com.mahasbr.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.CircularRequestDTO;
import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.entity.Circular;
import com.mahasbr.repository.CircularRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CircularServiceImpl implements CircularService {

    private static final String CIRCULAR_STORAGE_AREA = "circulars";
    private static final String DEFAULT_ACTIVITY = "general";
    private static final Set<String> CIRCULAR_ALLOWED_EXTENSIONS = Set.of("pdf");
    private static final Set<String> CIRCULAR_ALLOWED_MIME_TYPES = Set.of("application/pdf");

    private final CircularRepository circularRepository;
    private final FileStorageService fileStorageService;
    private final com.mahasbr.config.FileStorageProperties fileStorageProperties;

    @Override
    @Transactional
    public CircularResponseDTO createCircular(CircularRequestDTO dto) {
        String activity = resolveActivity(dto.getActivity(), null);
        String relativeFilePath = fileStorageService.store(
                dto.getFile(),
                CIRCULAR_STORAGE_AREA,
                activity,
                circularUploadPolicy());

        Circular circular = new Circular();
        circular.setSubject(dto.getSubject());
        circular.setCircularDate(dto.getDate());
        circular.setFilePath(relativeFilePath);

        return mapToResponse(circularRepository.save(circular));
    }

    @Override
    @Transactional
    public CircularResponseDTO updateCircular(CircularRequestDTO dto) {
        Circular existing = circularRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Circular not found with id: " + dto.getId()));

        String activity = resolveActivity(dto.getActivity(), existing.getFilePath());
        String relativeFilePath = existing.getFilePath();

        if (hasNewFile(dto.getFile())) {
            String newRelativeFilePath = fileStorageService.store(
                    dto.getFile(),
                    CIRCULAR_STORAGE_AREA,
                    activity,
                    circularUploadPolicy());
            fileStorageService.deleteIfExists(existing.getFilePath());
            relativeFilePath = newRelativeFilePath;
        } else if (shouldMoveExistingFile(existing.getFilePath(), activity)) {
            relativeFilePath = fileStorageService.move(existing.getFilePath(), CIRCULAR_STORAGE_AREA, activity);
        }

        existing.setSubject(dto.getSubject());
        existing.setCircularDate(dto.getDate());
        existing.setFilePath(relativeFilePath);

        return mapToResponse(circularRepository.save(existing));
    }

    @Override
    public List<CircularResponseDTO> getAllCirculars() {
        return circularRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CircularResponseDTO getCircularById(Long id) {
        Circular circular = circularRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circular not found with id: " + id));
        return mapToResponse(circular);
    }

    @Override
    @Transactional
    public void deleteCircular(Long id) {
        Circular circular = circularRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circular not found with id: " + id));
        fileStorageService.deleteIfExists(circular.getFilePath());
        circularRepository.delete(circular);
    }

    @Override
    public Resource getCircularFile(String relativePath) throws FileNotFoundException {
        try {
            return fileStorageService.loadAsResource(relativePath);
        } catch (FileNotFoundException ex) {
            if (!relativePath.contains("/")) {
                return fileStorageService.loadAsResource(CIRCULAR_STORAGE_AREA + "/" + relativePath);
            }
            throw ex;
        }
    }

    private CircularResponseDTO mapToResponse(Circular circular) {
        return CircularResponseDTO.builder()
                .id(circular.getId())
                .subject(circular.getSubject())
                .activity(extractActivity(circular.getFilePath()))
                .date(circular.getCircularDate())
                .fileUrl(circular.getFilePath())
                .build();
    }

    private boolean hasNewFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private boolean shouldMoveExistingFile(String existingRelativePath, String requestedActivity) {
        return StringUtils.hasText(existingRelativePath)
                && !requestedActivity.equalsIgnoreCase(extractActivity(existingRelativePath));
    }

    private String resolveActivity(String requestedActivity, String existingRelativePath) {
        if (StringUtils.hasText(requestedActivity)) {
            return requestedActivity;
        }

        String currentActivity = extractActivity(existingRelativePath);
        return StringUtils.hasText(currentActivity) ? currentActivity : DEFAULT_ACTIVITY;
    }

    private String extractActivity(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return DEFAULT_ACTIVITY;
        }

        String normalizedPath = relativePath.replace('\\', '/');
        String[] segments = normalizedPath.split("/");
        if (segments.length >= 3 && CIRCULAR_STORAGE_AREA.equalsIgnoreCase(segments[0])) {
            return segments[1];
        }

        return DEFAULT_ACTIVITY;
    }

    private FileUploadPolicy circularUploadPolicy() {
        long maxFileSizeMb = fileStorageProperties.getCircularMaxFileSizeMb() > 0
                ? fileStorageProperties.getCircularMaxFileSizeMb()
                : 25;
        return new FileUploadPolicy(maxFileSizeMb, CIRCULAR_ALLOWED_EXTENSIONS, CIRCULAR_ALLOWED_MIME_TYPES);
    }
}
