package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.User;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DistrictMasterServiceImpl implements DistrictMasterService {

	private final DistrictMasterRepository repository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

	public DistrictMasterServiceImpl(DistrictMasterRepository repository) {
		this.repository = repository;
	}

	@Override
	public DistrictMaster create(DistrictMaster district) {
		return repository.save(district);
	}

	@Override
	public DistrictMaster update(Long id, DistrictMaster district) {
		DistrictMaster existing = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("District not found"));

		existing.setDistrictName(district.getDistrictName());
		existing.setCensusStateCode(district.getCensusStateCode());
		existing.setIsActive(district.getIsActive());

		return repository.save(existing);
	}

	@Override
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("District not found");
		}
		repository.deleteById(id);
	}

	@Override
	public DistrictMaster getById(Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("District not found"));
	}

	@Override
	public List<DistrictMaster> getAll() {
		return repository.findAll();
	}

	@Override
	public void importDistrictsFromExcel(MultipartFile file) throws IOException {
		List<DistrictMaster> districtList = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0);
			int rowIndex = 0;

			for (Row row : sheet) {
				if (rowIndex++ == 0)
					continue; // Skip header row

				String censusDistrictCode = getCellStringValue(row.getCell(0));
				String districtName = getCellStringValue(row.getCell(1));
				String censusStateCode = getCellStringValue(row.getCell(2));
//                String isActiveStr = getCellStringValue(row.getCell(3));
//                Boolean isActive = Boolean.parseBoolean(isActiveStr.trim());

				// Optional: skip invalid rows
				if (censusDistrictCode == null || districtName == null || censusStateCode == null)
					continue;

				DistrictMaster district = new DistrictMaster();
				district.setCensusDistrictCode(censusDistrictCode);
				district.setDistrictName(districtName);
				district.setCensusStateCode(censusStateCode);
				district.setIsActive(true);

				districtList.add(district);
			}

			repository.saveAll(districtList);
		}
	}

	private String getCellStringValue(Cell cell) {
		if (cell == null)
			return null;
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue().trim();
	}

	@Override
	public List<DistrictMaster> findByIsActiveTrue() {
		return repository.findByIsActiveTrue();
	}

	@Override
	public Optional<DistrictMaster> findByCensusDistrictCode(String long1) {
		// TODO Auto-generated method stub
		return repository.findByCensusDistrictCode(long1);
	}

	@Override
	public List<DistrictMaster> findByIsActiveTrueBasedOnlogin() {

		Collection<? extends GrantedAuthority> userRoles = getUsersRole();
		String role = userRoles.stream().map(GrantedAuthority::getAuthority).findFirst().orElse("UNKNOWN");

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);
		// 2. Fetch all district codes under this divisio

		switch (role) {
		case "ROLE_DES_REGION":
			return districtMasterRepository.findByDivisionCodeAndIsActiveTrue(user.get().getDivisionCode());
		default:
			return districtMasterRepository.findByIsActiveTrue();
		}

	}

	public Collection<? extends GrantedAuthority> getUsersRole() {

		Collection<? extends GrantedAuthority> roles = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		return roles;
	}
}
