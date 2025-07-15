package com.mahasbr.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.VillageMaster;

public interface VillageMasterService {
	VillageMaster create(VillageMaster village);

	VillageMaster update(Long id, VillageMaster village);

	void delete(Long id);

	VillageMaster getById(Long id);

	List<VillageMaster> getAll();

	void importFromExcel(MultipartFile file) throws IOException;

}
