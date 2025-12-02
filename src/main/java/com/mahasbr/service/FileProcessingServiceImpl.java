package com.mahasbr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

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
	
	static {
        IOUtils.setByteArrayMaxOverride(200_000_000); // Allow large file
    }

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

//	private List<RegistryRowDTO> parseCsv(InputStream is) throws Exception {
//		List<RegistryRowDTO> list = new ArrayList<>();
//		try (CSVReader reader = new CSVReader(new InputStreamReader(is))) {
//			String[] headers = reader.readNext();
//			if (headers == null)
//				return list;
//			int rowNum = 1;
//			String[] line;
//			while ((line = reader.readNext()) != null) {
//				rowNum++;
//				Map<String, String> map = new LinkedHashMap<>();
//				for (int i = 0; i < headers.length; i++) {
//					String key = normalizeHeader(headers[i]);
//					String val = i < line.length ? line[i] : "";
//					map.put(key, val == null ? "" : val.trim());
//				}
//				Optional<String> err = validator.validate(map);
//				list.add(RegistryRowDTO.builder().rowData(map).valid(err.isEmpty()).errorMessage(err.orElse(""))
//						.rowNumber(rowNum).build());
//			}
//		}
//		return list;
//	}
	
	private List<RegistryRowDTO> parseCsv(InputStream is) throws Exception {
	    List<RegistryRowDTO> list = new ArrayList<>();

	    // Robust CSV parser for handling quoted values with commas
	    CSVParser parser = new CSVParserBuilder()
	            .withSeparator(',')
	            .withQuoteChar('"')
	            .withEscapeChar('\\') // Must be different from quote char
	            .withIgnoreQuotations(true)
	            .build();

	    try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(is))
	            .withCSVParser(parser)
	            .build()) {

	        String[] headers = reader.readNext();
	        if (headers == null || headers.length == 0) {
	            throw new IllegalArgumentException("CSV file does not contain a header row!");
	        }

	        int expectedColumnCount = headers.length;

	        // Normalize headers upfront
	        String[] normalizedHeaders = new String[expectedColumnCount];
	        for (int i = 0; i < expectedColumnCount; i++) {
	            normalizedHeaders[i] = normalizeHeader(headers[i]);
	        }

	        int rowNum = 1;
	        String[] line;

	        while ((line = reader.readNext()) != null) {
	            rowNum++;

	            // Skip blank rows (one-column with empty string too)
	            if (line.length == 0 || (line.length == 1 && line[0].trim().isEmpty())) {
	                continue;
	            }

	            // If row has fewer columns, pad it
	            if (line.length < expectedColumnCount) {
	                line = Arrays.copyOf(line, expectedColumnCount);
	                for (int i = 0; i < expectedColumnCount; i++) {
	                    if (line[i] == null) line[i] = "";
	                }
	            }

	            // If row has extra columns, merge extras into the last column
	            if (line.length > expectedColumnCount) {
	                String[] fixed = new String[expectedColumnCount];
	                System.arraycopy(line, 0, fixed, 0, expectedColumnCount - 1);
	                fixed[expectedColumnCount - 1] = String.join(" ",
	                        Arrays.copyOfRange(line, expectedColumnCount - 1, line.length));
	                line = fixed;
	            }

	            Map<String, String> map = new LinkedHashMap<>();
	            boolean allEmpty = true;

	            // Populate map and check if row is completely empty
	            for (int i = 0; i < expectedColumnCount; i++) {
	                String val = (line[i] == null ? "" : line[i].trim());
	                if (!val.isEmpty()) {
	                    allEmpty = false;
	                }
	                map.put(normalizedHeaders[i], val);
	            }

	            // ❌ Skip row only if ALL columns are empty
	            if (allEmpty) {
	            	logger.warn("Skipping row #{} because row is empty", rowNum);
	                continue;
	            }

	            Optional<String> err = validator.validate(map);

	            list.add(
	                RegistryRowDTO.builder()
	                        .rowData(map)
	                        .valid(err.isEmpty())
	                        .errorMessage(err.orElse(""))
	                        .rowNumber(rowNum)
	                        .build()
	            );
	        }

	    } catch (com.opencsv.exceptions.CsvMalformedLineException e) {
	        throw new RuntimeException("Invalid CSV format — ensure fields with commas are enclosed in quotes.", e);
	    }

	    return list;
	}



//	private List<RegistryRowDTO> parseExcel(InputStream is) throws Exception {
//		List<RegistryRowDTO> list = new ArrayList<>();
//		Workbook workbook = WorkbookFactory.create(is);
//		for (Sheet sheet : workbook) {
//			Iterator<Row> it = sheet.iterator();
//			if (!it.hasNext())
//				continue;
//			Row headerRow = it.next();
//			List<String> headers = new ArrayList<>();
//			for (Cell c : headerRow)
//				headers.add(normalizeHeader(cellToString(c)));
//			int rowNum = 1;
//			while (it.hasNext()) {
//				Row r = it.next();
//				rowNum++;
//				Map<String, String> map = new LinkedHashMap<>();
//				for (int i = 0; i < headers.size(); i++) {
//					Cell cell = r.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//					map.put(headers.get(i), cellToString(cell).trim());
//				}
//				Optional<String> err = validator.validate(map);
//				list.add(RegistryRowDTO.builder().rowData(map).valid(err.isEmpty()).errorMessage(err.orElse(""))
//						.rowNumber(rowNum).build());
//			}
//		}
//		workbook.close();
//		return list;
//	}
	
	private List<RegistryRowDTO> parseExcel(InputStream is) throws Exception {
	    List<RegistryRowDTO> list = new ArrayList<>();

	    OPCPackage pkg = OPCPackage.open(is);
	    XSSFWorkbook workbook = new XSSFWorkbook(pkg);

	    for (Sheet sheet : workbook) {
	        Iterator<Row> it = sheet.iterator();
	        if (!it.hasNext())
	            continue;

	        Row headerRow = it.next();
	        List<String> headers = new ArrayList<>();
	        for (Cell c : headerRow) {
	            headers.add(normalizeHeader(cellToString(c)));
	        }

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

	            list.add(
	                RegistryRowDTO.builder()
	                    .rowData(map)
	                    .valid(err.isEmpty())
	                    .errorMessage(err.orElse(""))
	                    .rowNumber(rowNum)
	                    .build()
	            );
	        }
	    }

	    workbook.close();
	    pkg.close();
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
