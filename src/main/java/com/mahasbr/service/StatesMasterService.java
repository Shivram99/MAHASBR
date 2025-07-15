package com.mahasbr.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.StatesMaster;

public interface StatesMasterService {
	StatesMaster create(StatesMaster state);

	StatesMaster update(Long id, StatesMaster state);

	StatesMaster getById(Long id);

	List<StatesMaster> getAll();

	void delete(Long id);
	
	void importStatesFromExcel(MultipartFile file) throws IOException;


}
