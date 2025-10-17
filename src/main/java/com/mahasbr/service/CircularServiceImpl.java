package com.mahasbr.service;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.CircularRequestDTO;
import com.mahasbr.dto.CircularResponseDTO;
import com.mahasbr.entity.Circular;
import com.mahasbr.repository.CircularRepository;
import com.mahasbr.util.FileStorageUtil;

@Service
//@PropertySource("application.properties")
public class CircularServiceImpl implements CircularService {
	private static final Logger logger = LoggerFactory.getLogger(CircularServiceImpl.class);

//	@Autowired
//	private Environment env;

	@Autowired
	private CircularRepository circularRepository;
	
	@Autowired
	private FileStorageUtil fileStorageUtil;

	@Override
	public CircularResponseDTO createCircular(CircularRequestDTO dto) {
		MultipartFile file = dto.getFile();
		
		String relativeFilePath = fileStorageUtil.saveFile(dto.getFile(), "circulars");
		
		Circular circular = new Circular();
		circular.setSubject(dto.getSubject());
		circular.setCircularDate(dto.getDate());
		circular.setFilePath(relativeFilePath);


		circular = circularRepository.save(circular);

		return mapToResponse(circular);
	}
	
	@Override
	public CircularResponseDTO updateCircular(CircularRequestDTO dto) {
		Circular existing = circularRepository.findById(dto.getId())
		        .orElseThrow(() -> new RuntimeException("Circular not found with id: " + dto.getId()));
		MultipartFile file = dto.getFile();
		
		String relativeFilePath = fileStorageUtil.saveFile(dto.getFile(), "circulars");

		existing.setSubject(dto.getSubject());
		existing.setCircularDate(dto.getDate());
		existing.setFilePath(relativeFilePath);


		Circular circular = circularRepository.save(existing);

		return mapToResponse(circular);
	}

	@Override
	public List<CircularResponseDTO> getAllCirculars() {
		return circularRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	public CircularResponseDTO getCircularById(Long id) {
		Circular circular = circularRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Circular not found with id: " + id));
		return mapToResponse(circular);
	}

	@Override
	public void deleteCircular(Long id) {
		circularRepository.deleteById(id);
	}

	private CircularResponseDTO mapToResponse(Circular circular) {
		return CircularResponseDTO.builder().id(circular.getId()).subject(circular.getSubject())
				.date(circular.getCircularDate()).fileUrl(circular.getFilePath()).build();
	}
	  @Override
	    public Resource getCircularFile(String relativePath) throws FileNotFoundException {
	        Path filePath = fileStorageUtil.getAbsolutePath(relativePath);

	        if (!Files.exists(filePath)) {
	            throw new FileNotFoundException("File not found: " + relativePath);
	        }

	        try {
	            return new UrlResource(filePath.toUri());
	        } catch (MalformedURLException e) {
	            throw new RuntimeException("Invalid file URL", e);
	        }
	    }
}
