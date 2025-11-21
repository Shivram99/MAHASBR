package com.mahasbr.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.dto.RequestFormDTO;
import com.mahasbr.entity.RequestFormEntity;
import com.mahasbr.repository.RequestFormRepository;

@Service
public class RequestFormService {
	
	@Autowired
	RequestFormRepository requestFormRepository;
	
	public String saveRequest(RequestFormDTO request) {

	    String reqId = generate();

	    RequestFormEntity entity = RequestFormEntity.builder()
	            .requestId(reqId)
	            .name(request.getName())
	            .email(request.getEmail())
	            .mobile(request.getMobile())
	            .message(request.getMessage())
	            .build();

	    requestFormRepository.save(entity);

	    return reqId;
	}

	 public String generate() {
	        String date = LocalDate.now().toString().replace("-", "");
	        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	        return "REQ-" + date + "-" + random;
	    }

}
