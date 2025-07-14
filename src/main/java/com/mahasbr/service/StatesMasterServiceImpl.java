package com.mahasbr.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.entity.StatesMaster;
import com.mahasbr.exception.ResourceNotFoundException;
import com.mahasbr.repository.StatesMasterRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StatesMasterServiceImpl implements StatesMasterService {

    private final StatesMasterRepository repository;

    @Override
    public StatesMaster create(StatesMaster state) {
        if (repository.existsByCensusStateCode(state.getCensusStateCode())) {
            throw new IllegalArgumentException("Census state code already exists.");
        }
        return repository.save(state);
    }

    @Override
    public StatesMaster update(Long id, StatesMaster updatedState) {
        StatesMaster existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with ID " + id));

        existing.setStateName(updatedState.getStateName());
        existing.setIsActive(updatedState.getIsActive());
        return repository.save(existing);
    }

    @Override
    public StatesMaster getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State not found with ID " + id));
    }

    @Override
    public List<StatesMaster> getAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("State not found with ID " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    public void importStatesFromExcel(MultipartFile file) throws IOException {
        List<StatesMaster> stateList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                if (firstRow) { // Skip header
                    firstRow = false;
                    continue;
                }

                String censusCode = row.getCell(0).getStringCellValue();

                String stateName = row.getCell(1).getStringCellValue();
//                Boolean isActive = row.getCell(2).getBooleanCellValue();

                if (censusCode != null && stateName != null && !stateName.trim().isEmpty()) {
                    // Avoid duplicate census code
                    if (!repository.existsByCensusStateCode(censusCode)) {
                        StatesMaster state = new StatesMaster();
                        state.setCensusStateCode(censusCode);
                        state.setStateName(stateName);
                        state.setIsActive(true);
                        stateList.add(state);
                    }
                }
            }

            repository.saveAll(stateList);
        } catch (Exception e) {
            throw new IOException("Failed to parse Excel file: " + e.getMessage(), e);
        }
    }

}

