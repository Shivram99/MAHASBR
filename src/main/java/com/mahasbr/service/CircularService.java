package com.mahasbr.service;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.core.io.Resource;

import com.mahasbr.dto.CircularRequestDTO;
import com.mahasbr.dto.CircularResponseDTO;

public interface CircularService {
	CircularResponseDTO createCircular(CircularRequestDTO dto);
	
	CircularResponseDTO updateCircular(CircularRequestDTO dto);

	/** Every circular, active or not (for the admin list). */
	List<CircularResponseDTO> getAllCirculars();

	/** Only active circulars, newest first (for the public site). */
	List<CircularResponseDTO> getActiveCirculars();

	/** Shows (true) or hides (false) a circular on the public site. */
	CircularResponseDTO setActive(Long id, boolean active);

	CircularResponseDTO getCircularById(Long id);

	void deleteCircular(Long id);
	
	Resource getCircularFile(String relativePath) throws FileNotFoundException;
}
