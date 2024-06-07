package com.mahasbr.service;

import com.mahasbr.entity.DetailsPage;

public interface ImportExcelSheetService {

	void save(DetailsPage details);

	DetailsPage findOrgData(DetailsPage details);

}
