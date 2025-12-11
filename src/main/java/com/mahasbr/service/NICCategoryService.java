package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.NICCategoryDTO;
import com.mahasbr.entity.NICCategoryEntity;
import com.mahasbr.entity.NICClassEntity;
import com.mahasbr.entity.NICCodeEntity;
import com.mahasbr.entity.NICDivisionEntity;
import com.mahasbr.entity.NICGroupEntity;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.NICCategoryRepository;
import com.mahasbr.repository.NICClassRepository;
import com.mahasbr.repository.NICCodeRepository;
import com.mahasbr.repository.NICDivisionRepository;
import com.mahasbr.repository.NICGroupRepository;
@Service
public class NICCategoryService {

    @Autowired
    private NICCategoryRepository nicCategoryRepository;

    @Autowired
    private NICDivisionRepository divisionRepo;
    @Autowired
    private NICGroupRepository groupRepo;
    @Autowired
    private NICClassRepository classRepo;
    @Autowired
    private NICCodeRepository codeRepo;

    // Get list without child data
    public List<NICCategoryDTO> getAllCategories() {
        List<NICCategoryEntity> list = nicCategoryRepository.findBasicCategories();
        return list.stream()
                .map(c -> new NICCategoryDTO(
                        c.getCategoryCode(),
                        c.getDescription(),
                        c.getIsActive()))
                .collect(Collectors.toList());
    }

    public NICCategoryDTO getCategoryByCode(String categoryCode) {
        NICCategoryEntity c = getCategoryEntity(categoryCode);
        return new NICCategoryDTO(
                c.getCategoryCode(),
                c.getDescription(),
                c.getIsActive());
    }

    public NICCategoryDTO createCategory(NICCategoryDTO dto) {
        NICCategoryEntity c = new NICCategoryEntity(
            dto.getCategoryCode(),
            dto.getDescription(),
            null,
            dto.getIsActive()
        );

        NICCategoryEntity saved = nicCategoryRepository.save(c);
        return new NICCategoryDTO(saved.getCategoryCode(), saved.getDescription(), saved.getIsActive());
    }

    public NICCategoryDTO updateCategory(String categoryCode, NICCategoryDTO dto) {
        NICCategoryEntity existing = getCategoryEntity(categoryCode);

        existing.setDescription(dto.getDescription());
        existing.setIsActive(dto.getIsActive());

        NICCategoryEntity updated = nicCategoryRepository.save(existing);

        return new NICCategoryDTO(updated.getCategoryCode(), updated.getDescription(), updated.getIsActive());
    }

    public NICCategoryDTO toggleStatus(String categoryCode) {
        NICCategoryEntity category = getCategoryEntity(categoryCode);

        category.setIsActive(category.getIsActive().equals("Y") ? "N" : "Y");
        NICCategoryEntity updated = nicCategoryRepository.save(category);

        return new NICCategoryDTO(updated.getCategoryCode(), updated.getDescription(), updated.getIsActive());
    }

    private NICCategoryEntity getCategoryEntity(String code) {
        return nicCategoryRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found with code: " + code)
                );
    }

    // IMPORT EXCEL: remains same (using entities)
    @Transactional
    public void importFromExcel(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row r = sheet.getRow(rowIndex);
                if (r == null) continue;

                String categoryCode = getCellValue(r, 0);
                String categoryName = getCellValue(r, 1);
                String divisionCode = getCellValue(r, 2);
                String divisionName = getCellValue(r, 3);
                String groupCode = getCellValue(r, 4);
                String groupName = getCellValue(r, 5);
                String classCode = getCellValue(r, 6);
                String className = getCellValue(r, 7);
                String nicCodeValue = getCellValue(r, 8);
                String nicCodeName = getCellValue(r, 9);

                NICCategoryEntity category = nicCategoryRepository.findById(categoryCode)
                        .orElseGet(() ->
                                nicCategoryRepository.save(new NICCategoryEntity(categoryCode, categoryName, null, "Y")));

                NICDivisionEntity division = divisionRepo.findById(divisionCode)
                        .orElseGet(() ->
                                divisionRepo.save(new NICDivisionEntity(divisionCode, divisionName, category, null, "Y")));

                NICGroupEntity group = groupRepo.findById(groupCode)
                        .orElseGet(() ->
                                groupRepo.save(new NICGroupEntity(groupCode, groupName, division, null, "Y")));

                NICClassEntity nicClass = classRepo.findById(classCode)
                        .orElseGet(() ->
                                classRepo.save(new NICClassEntity(classCode, className, group, null, "Y")));

                if (!codeRepo.existsByCode(nicCodeValue)) {
                    NICCodeEntity code = new NICCodeEntity();
                    code.setCode(nicCodeValue);
                    code.setDescription(nicCodeName);
                    code.setNicClass(nicClass);
                    codeRepo.save(code);
                }
            }
        }
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}

