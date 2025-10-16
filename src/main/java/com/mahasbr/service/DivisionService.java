package com.mahasbr.service;

import java.util.List;
import java.util.Optional;

import com.mahasbr.dto.DivisionDto;
import com.mahasbr.entity.DivisionMaster;

public interface DivisionService {
    DivisionDto createDivision(DivisionDto divisionDto);
    DivisionDto updateDivision(Long id, DivisionDto divisionDto);
    void deleteDivision(Long id);
    DivisionDto getDivisionById(Long id);
    List<DivisionDto> getAllDivisions();
	
}
