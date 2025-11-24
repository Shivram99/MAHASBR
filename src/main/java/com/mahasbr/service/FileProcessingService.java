package com.mahasbr.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.RegistryRowDTO;

public interface FileProcessingService {
  List<RegistryRowDTO> parseForPreview(MultipartFile file) throws Exception;
  void processAndSave(MultipartFile file, String fileId) throws Exception;
  void saveRows(List<Map<String,String>> rows) throws Exception;
}
