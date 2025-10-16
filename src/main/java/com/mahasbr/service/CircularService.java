package com.mahasbr.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.core.io.Resource;

import com.mahasbr.dto.CircularRequestDTO;
import com.mahasbr.dto.CircularResponseDTO;

public interface CircularService {
	CircularResponseDTO createCircular(CircularRequestDTO dto);
	
	CircularResponseDTO updateCircular(CircularRequestDTO dto);

	List<CircularResponseDTO> getAllCirculars();

	CircularResponseDTO getCircularById(Long id);

	void deleteCircular(Long id);
	
	Resource getCircularFile(String relativePath) throws FileNotFoundException;
}
