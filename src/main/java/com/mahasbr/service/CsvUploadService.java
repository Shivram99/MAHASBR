package com.mahasbr.service;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.CsvUploadFailedPageResponse;
import com.mahasbr.dto.CsvUploadInitResponse;
import com.mahasbr.dto.CsvUploadPreviewResponse;
import com.mahasbr.dto.CsvUploadStatusResponse;
import com.mahasbr.dto.CsvUploadSuccessPageResponse;

public interface CsvUploadService {
	CsvUploadInitResponse upload(MultipartFile file, String username);

	CsvUploadPreviewResponse generatePreview(String jobId, int page, int size);

	CsvUploadPreviewResponse getPreviewPage(String jobId, int page, int size);

	CsvUploadStatusResponse startProcessing(String jobId, String username);

	CsvUploadStatusResponse pauseProcessing(String jobId);

	CsvUploadStatusResponse resumeProcessing(String jobId, String username);

	CsvUploadStatusResponse cancelProcessing(String jobId);

	CsvUploadStatusResponse getStatus(String jobId);

	CsvUploadSuccessPageResponse getSuccessPage(String jobId, int page, int size);

	CsvUploadFailedPageResponse getFailedPage(String jobId, int page, int size);
}
