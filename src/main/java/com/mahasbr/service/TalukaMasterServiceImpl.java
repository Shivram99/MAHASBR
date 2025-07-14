package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.TalukaMaster;
import com.mahasbr.repository.TalukaMasterRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class TalukaMasterServiceImpl implements TalukaMasterService {

    private final TalukaMasterRepository repository;

    public TalukaMasterServiceImpl(TalukaMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public TalukaMaster create(TalukaMaster taluka) {
        return repository.save(taluka);
    }

    @Override
    public TalukaMaster update(Long id, TalukaMaster taluka) {
        TalukaMaster existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Taluka not found"));

        existing.setTalukaName(taluka.getTalukaName());
        existing.setCensusDistrictCode(taluka.getCensusDistrictCode());
        existing.setIsActive(taluka.getIsActive());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Taluka not found");
        }
        repository.deleteById(id);
    }

    @Override
    public TalukaMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Taluka not found"));
    }

    @Override
    public List<TalukaMaster> getAll() {
        return repository.findAll();
    }
    
    @Override
    public void importTalukasFromExcel(MultipartFile file) throws IOException {
        List<TalukaMaster> talukas = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowIndex = 0;

            for (Row row : sheet) {
                if (rowIndex++ == 0) continue; // skip header row

                String censusTalukaCode = getCellValue(row.getCell(0));
                String talukaName = getCellValue(row.getCell(1));
                String censusDistrictCode = getCellValue(row.getCell(2));
//                String isActiveStr = getCellValue(row.getCell(3));
//                boolean isActive = Boolean.parseBoolean(isActiveStr);

                if (censusTalukaCode == null || talukaName == null || censusDistrictCode == null) continue;

                TalukaMaster taluka = new TalukaMaster();
                taluka.setCensusTalukaCode(censusTalukaCode);
                taluka.setTalukaName(talukaName);
                taluka.setCensusDistrictCode(censusDistrictCode);
                taluka.setIsActive(true);

                talukas.add(taluka);
            }

            repository.saveAll(talukas);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}

