package com.mahasbr.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mahasbr.dto.DivisionDto;
import com.mahasbr.entity.DivisionMaster;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.mapper.DivisionMapper;
import com.mahasbr.repository.DivisionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {

	private final DivisionRepository divisionRepository;

	@Override
	public DivisionDto createDivision(DivisionDto divisionDto) {
		if (divisionRepository.existsByDivisionCode(divisionDto.getDivisionCode())) {
			throw new IllegalArgumentException("Division code already exists!");
		}
		DivisionMaster entity = DivisionMapper.toEntity(divisionDto);
		return DivisionMapper.toDto(divisionRepository.save(entity));
	}

	@Override
	public DivisionDto updateDivision(Long id, DivisionDto divisionDto) {
		DivisionMaster existing = divisionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Division not found with id " + id));

		existing.setDivisionName(divisionDto.getDivisionName());
		existing.setDivisionCode(divisionDto.getDivisionCode());
		existing.setIsActive(divisionDto.getIsActive());

		return DivisionMapper.toDto(divisionRepository.save(existing));
	}

	@Override
	public void deleteDivision(Long id) {
		DivisionMaster existing = divisionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Division not found with id " + id));
		divisionRepository.delete(existing);
	}

	@Override
	public DivisionDto getDivisionById(Long id) {
		DivisionMaster entity = divisionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Division not found with id " + id));
		return DivisionMapper.toDto(entity);
	}

	@Override
	public List<DivisionDto> getAllDivisions() {
		return divisionRepository.findAll().stream().map(DivisionMapper::toDto).collect(Collectors.toList());
	}
}
