package com.mahasbr.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.CsvUploadFailedPageResponse;
import com.mahasbr.dto.CsvUploadInitResponse;
import com.mahasbr.dto.CsvUploadPreviewResponse;
import com.mahasbr.dto.CsvUploadStatusResponse;
import com.mahasbr.dto.CsvUploadSuccessPageResponse;
import com.mahasbr.service.CsvUploadService;
import com.mahasbr.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({ "/api/auth/csv-upload", "/api/csv-upload" })
@RequiredArgsConstructor
public class CsvUploadController {

	private final CsvUploadService csvUploadService;

	@PostMapping(value = { "", "/upload" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<CsvUploadInitResponse>> upload(@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		String username = authentication != null ? authentication.getName() : "SYSTEM";
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.upload(file, username), "File uploaded successfully."));
	}

	@PostMapping({ "/{jobId}/preview", "/preview/{jobId}" })
	public ResponseEntity<ApiResponse<CsvUploadPreviewResponse>> generatePreview(@PathVariable String jobId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
		return ResponseEntity.ok(
				ApiResponse.ok(csvUploadService.generatePreview(jobId, page, size), "Preview generated successfully."));
	}

	@GetMapping({ "/{jobId}/preview", "/preview/{jobId}" })
	public ResponseEntity<ApiResponse<CsvUploadPreviewResponse>> getPreviewPage(@PathVariable String jobId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
		return ResponseEntity.ok(
				ApiResponse.ok(csvUploadService.getPreviewPage(jobId, page, size), "Preview page fetched successfully."));
	}

	@PostMapping({ "/{jobId}/start", "/process/{jobId}" })
	public ResponseEntity<ApiResponse<CsvUploadStatusResponse>> start(@PathVariable String jobId,
			Authentication authentication) {
		String username = authentication != null ? authentication.getName() : "SYSTEM";
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.startProcessing(jobId, username),
				"Processing started."));
	}

	@PostMapping("/{jobId}/pause")
	public ResponseEntity<ApiResponse<CsvUploadStatusResponse>> pause(@PathVariable String jobId) {
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.pauseProcessing(jobId), "Pause requested."));
	}

	@PostMapping("/{jobId}/resume")
	public ResponseEntity<ApiResponse<CsvUploadStatusResponse>> resume(@PathVariable String jobId,
			Authentication authentication) {
		String username = authentication != null ? authentication.getName() : "SYSTEM";
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.resumeProcessing(jobId, username),
				"Processing resumed."));
	}

	@PostMapping("/{jobId}/cancel")
	public ResponseEntity<ApiResponse<CsvUploadStatusResponse>> cancel(@PathVariable String jobId) {
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.cancelProcessing(jobId), "Cancellation requested."));
	}

	@GetMapping({ "/{jobId}/status", "/status/{jobId}" })
	public ResponseEntity<ApiResponse<CsvUploadStatusResponse>> status(@PathVariable String jobId) {
		return ResponseEntity
				.ok(ApiResponse.ok(csvUploadService.getStatus(jobId), "Upload status fetched successfully."));
	}

	@GetMapping({ "/{jobId}/success-records", "/results/{jobId}/success" })
	public ResponseEntity<ApiResponse<CsvUploadSuccessPageResponse>> successPage(@PathVariable String jobId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.getSuccessPage(jobId, page, size),
				"Success records fetched successfully."));
	}

	@GetMapping({ "/{jobId}/failed-records", "/results/{jobId}/failed" })
	public ResponseEntity<ApiResponse<CsvUploadFailedPageResponse>> failedPage(@PathVariable String jobId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
		return ResponseEntity.ok(ApiResponse.ok(csvUploadService.getFailedPage(jobId, page, size),
				"Failed records fetched successfully."));
	}
}
