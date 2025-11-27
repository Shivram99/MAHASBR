package com.mahasbr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.RegistryRowDTO;
import com.mahasbr.dto.UploadResultRecordes;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.User;
import com.mahasbr.mapper.RegistryMapper;
import com.mahasbr.repository.CensusEntityRepository;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.TalukaMasterRepository;
import com.mahasbr.repository.UserRepository;
import com.mahasbr.repository.VillageMasterRepository;
import com.mahasbr.util.LocationGenerator;
import com.mahasbr.util.RowValidator;
import com.mahasbr.util.UploadProgressStore;
import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileProcessingServiceImpl implements FileProcessingService {

	private static final Logger logger = LoggerFactory.getLogger(FileProcessingServiceImpl.class);
	private final UploadProgressStore progressStore;
	private final MstRegistryDetailsPageRepository repository;
	private final RegistryMapper mapper;
	private final RowValidator validator;
	private final UploadResultRecordes uploadResultRecordes;

	@Autowired
	private MstRegistryDetailsPageServiceImpl mstRegistryDetailsPageServiceImpl;

	@Autowired
	UserRepository userRepository;

	@Autowired
	BrnGeneratorService brnGeneratorService;

	@Autowired
	CensusEntityRepository censusEntityRepository;

	@Autowired
	DistrictMasterRepository districtMasterRepository;

	@Autowired
	TalukaMasterRepository talukaMasterRepository;

	@Autowired
	VillageMasterRepository villageMasterRepository;

	@Autowired
	LocationGenerator locationGenerator;

	Long userId = 0L;

	public Long getLoginUsernameId() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			userId = user.get().getRegistry().getId();
		}
		return userId;
	}

	@Override
	public List<RegistryRowDTO> parseForPreview(MultipartFile file) throws Exception {
		String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
		if (name.endsWith(".csv")) {
			return parseCsv(file.getInputStream());
		} else {
			return parseExcel(file.getInputStream());
		}
	}

//	@Async
//	public void processAndSave(byte[] fileBytes, String fileId, String fileName, Authentication authentication) {
//		try {
//			// Initialize progress
//			progressStore.set(fileId, 0);
//
//			InputStream is = new ByteArrayInputStream(fileBytes);
//			SecurityContext context = SecurityContextHolder.createEmptyContext();
//			context.setAuthentication(authentication);
//			SecurityContextHolder.setContext(context);
//
//			// Parse file into row DTOs
//			List<RegistryRowDTO> rows = fileName.toLowerCase().endsWith(".csv") ? parseCsv(is) : parseExcel(is);
//
//			int total = Math.max(1, rows.size());
//			int processed = 0;
//
//			List<MstRegistryDetailsPageEntity> toSave = new ArrayList<>();
//			List<RegistryRowDTO> savedRows = new ArrayList<>(); // <-- STORE only valid rows for UI
//
//			// Loop through parsed rows
//			for (RegistryRowDTO r : rows) {
//				processed++;
//
//				// Update upload progress %
//				progressStore.set(fileId, (processed * 100) / total);
//
//				// Only process valid rows
////	            if (r.isValid()) {
//				try {
//					// Convert row into DB entity
//					MstRegistryDetailsPageEntity ent = mapper.toEntity(r.getRowData());
//
//					// Auto-generate BRN if missing
//					if (ent.getBrnNo() == null || ent.getBrnNo().isBlank()) {
//						ent.setBrnNo(brnGeneratorService.generateBrn("27"));
//						ent.setLocationCode(getLocationCode(ent.getDistrict(),ent.getTaluka(),ent.getTownVillage()));
//						ent.setRegUserId(Integer.parseInt(getLoginUsernameId().toString()));
//					}
//
//					toSave.add(ent);
//					savedRows.add(r); // <-- Add to UI return list
//
//				} catch (Exception ex) {
//					ex.printStackTrace();
//				}
//			}
////	        }
//
//			// Save all valid rows into DB
//			if (!toSave.isEmpty()) {
//				repository.saveAll(toSave);
//			}
//
//			// Save result for frontend to fetch later
//			uploadResultRecordes.set(fileId, savedRows);
//
//			// Mark upload complete
//			progressStore.set(fileId, 100);
//
//		} catch (Exception ex) {
//			// Mark as failed
//			progressStore.set(fileId, -1);
//			ex.printStackTrace();
//		}
//	}

	@Async
	public void processAndSave(byte[] fileBytes, String fileId, String fileName, Authentication authentication) {
		try {
			// Initialize progress
			progressStore.set(fileId, 0);

			InputStream is = new ByteArrayInputStream(fileBytes);
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);

			// Parse file rows
			List<RegistryRowDTO> rows = fileName.toLowerCase().endsWith(".csv") ? parseCsv(is) : parseExcel(is);

			int total = Math.max(1, rows.size());
			int processed = 0;

			List<RegistryRowDTO> savedRows = new ArrayList<>();

			// PROCESS RECORDS ONE BY ONE
			for (RegistryRowDTO r : rows) {
				processed++;

				try {
					// Convert row into entity
					MstRegistryDetailsPageEntity ent = mapper.toEntity(r.getRowData());

					// Auto-generate BRN if needed
					if (ent.getBrnNo() == null || ent.getBrnNo().isBlank()) {
						ent.setBrnNo(brnGeneratorService.generateBrn("27"));
						ent.setLocationCode(locationGenerator.getLocationCode(ent.getDistrict(), ent.getTaluka(),
								ent.getTownVillage()));
						ent.setRegUserId(Integer.parseInt(getLoginUsernameId().toString()));
					}

					// SAVE SINGLE RECORD IN DB
					repository.save(ent);

					// Add to UI list
					savedRows.add(r);

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// Update progress % after each record
				progressStore.set(fileId, (processed * 100) / total);
			}

			// Store result for frontend request
			uploadResultRecordes.set(fileId, savedRows);

			// Mark complete
			progressStore.set(fileId, 100);

		} catch (Exception ex) {
			progressStore.set(fileId, -1);
			ex.printStackTrace();
		}
	}

	@Override
	public void saveRows(List<Map<String, String>> rows) throws Exception {
		List<MstRegistryDetailsPageEntity> entities = new ArrayList<>();
		for (Map<String, String> r : rows) {
			Optional<String> err = validator.validate(r);
			if (err.isEmpty()) {
				MstRegistryDetailsPageEntity e = mapper.toEntity(r);
				if (e.getBrnNo() == null || e.getBrnNo().isBlank())
//					e.setBrnNo(generateBrn());
//				e.setRegUserId(Integer.parseInt(getLoginUsernameId().toString()));
					entities.add(e);
			}
		}
		if (!entities.isEmpty())
			repository.saveAll(entities);
	}

	private List<RegistryRowDTO> parseCsv(InputStream is) throws Exception {
		List<RegistryRowDTO> list = new ArrayList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(is))) {
			String[] headers = reader.readNext();
			if (headers == null)
				return list;
			int rowNum = 1;
			String[] line;
			while ((line = reader.readNext()) != null) {
				rowNum++;
				Map<String, String> map = new LinkedHashMap<>();
				for (int i = 0; i < headers.length; i++) {
					String key = normalizeHeader(headers[i]);
					String val = i < line.length ? line[i] : "";
					map.put(key, val == null ? "" : val.trim());
				}
				Optional<String> err = validator.validate(map);
				list.add(RegistryRowDTO.builder().rowData(map).valid(err.isEmpty()).errorMessage(err.orElse(""))
						.rowNumber(rowNum).build());
			}
		}
		return list;
	}

	private List<RegistryRowDTO> parseExcel(InputStream is) throws Exception {
		List<RegistryRowDTO> list = new ArrayList<>();
		Workbook workbook = WorkbookFactory.create(is);
		for (Sheet sheet : workbook) {
			Iterator<Row> it = sheet.iterator();
			if (!it.hasNext())
				continue;
			Row headerRow = it.next();
			List<String> headers = new ArrayList<>();
			for (Cell c : headerRow)
				headers.add(normalizeHeader(cellToString(c)));
			int rowNum = 1;
			while (it.hasNext()) {
				Row r = it.next();
				rowNum++;
				Map<String, String> map = new LinkedHashMap<>();
				for (int i = 0; i < headers.size(); i++) {
					Cell cell = r.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					map.put(headers.get(i), cellToString(cell).trim());
				}
				Optional<String> err = validator.validate(map);
				list.add(RegistryRowDTO.builder().rowData(map).valid(err.isEmpty()).errorMessage(err.orElse(""))
						.rowNumber(rowNum).build());
			}
		}
		workbook.close();
		return list;
	}

	private String normalizeHeader(String header) {
		if (header == null)
			return "";
		return header.trim();
	}

	private String cellToString(Cell cell) {
		if (cell == null)
			return "";
		return switch (cell.getCellType()) {
		case STRING -> cell.getStringCellValue();
		case NUMERIC -> {
			if (DateUtil.isCellDateFormatted(cell))
				yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
			double d = cell.getNumericCellValue();
			String s = Double.toString(d);
			if (s.endsWith(".0"))
				s = s.substring(0, s.length() - 2);
			yield s;
		}
		case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());
		case FORMULA -> cell.getCellFormula();
		default -> "";
		};
	}

	/**
	 * Converts null values to empty string and trims.
	 */
	private String safeCode(Object code) {
		return code == null ? "" : code.toString().trim();
	}

}
