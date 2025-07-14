package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.lang3.StringUtils;

import com.mahasbr.entity.VillageMaster;
import com.mahasbr.repository.VillageMasterRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class VillageMasterServiceImpl implements VillageMasterService {

    private final VillageMasterRepository repository;

    public VillageMasterServiceImpl(VillageMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public VillageMaster create(VillageMaster village) {
        return repository.save(village);
    }

    @Override
    public VillageMaster update(Long id, VillageMaster village) {
        VillageMaster existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Village not found with ID: " + id));

        existing.setVillageName(village.getVillageName());
        existing.setCensusTalukaCode(village.getCensusTalukaCode());
        existing.setCensusDistrictCode(village.getCensusDistrictCode());
        existing.setIsActive(village.getIsActive());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Village not found");
        }
        repository.deleteById(id);
    }

    @Override
    public VillageMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Village not found"));
    }

    @Override
    public List<VillageMaster> getAll() {
        return repository.findAll();
    }
    
    @Override
    public void importFromExcel(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;

            for (Row row : sheet) {
                if (rowIndex++ == 0) continue; // Skip header row

                String censusVillageCode = getCellValue(row.getCell(0));
                String villageName = getCellValue(row.getCell(1));
                String censusTalukaCode = getCellValue(row.getCell(2));
                String censusDistrictCode = getCellValue(row.getCell(3));
//                String isActiveStr = getCellValue(row.getCell(4));

                if (StringUtils.isAnyBlank(censusVillageCode, villageName, censusTalukaCode, censusDistrictCode)) {
                    System.out.println("Skipping row " + rowIndex + ": missing required fields.");
                    continue;
                }

                Optional<VillageMaster> existing = repository.findByCensusVillageCode(censusVillageCode);
                if (existing.isPresent()) {
                    System.out.println("Skipping duplicate village code: " + censusVillageCode);
                    continue;
                }

                VillageMaster village = new VillageMaster();
                village.setCensusVillageCode(censusVillageCode);
                village.setVillageName(villageName);
                village.setCensusTalukaCode(censusTalukaCode);
                village.setCensusDistrictCode(censusDistrictCode);
                village.setIsActive(true);

                repository.save(village);
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue().trim();
        return value.isEmpty() ? null : value;
    }


}

