package com.mahasbr.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mahasbr.dto.RegistryRowDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.mapper.RegistryMapper;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.util.RowValidator;
import com.mahasbr.util.UploadProgressStore;
import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileProcessingServiceImpl implements FileProcessingService {

	private final UploadProgressStore progressStore;
	private final MstRegistryDetailsPageRepository repository;
	private final RegistryMapper mapper;
	private final RowValidator validator;
	
	@Autowired
	private MstRegistryDetailsPageServiceImpl mstRegistryDetailsPageServiceImpl;

	@Override
	public List<RegistryRowDTO> parseForPreview(MultipartFile file) throws Exception {
		String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
		if (name.endsWith(".csv")) {
			return parseCsv(file.getInputStream());
		} else {
			return parseExcel(file.getInputStream());
		}
	}

	@Async
	public void processAndSave(byte[] fileBytes, String fileId, String fileName) {
		try {
			progressStore.set(fileId, 0);

			InputStream is = new ByteArrayInputStream(fileBytes);

			List<RegistryRowDTO> rows;

			if (fileName.toLowerCase().endsWith(".csv")) {
				rows = parseCsv(is); // no more multipart needed
			} else {
				rows = parseExcel(is); // reads directly from memory
			}

			int total = Math.max(1, rows.size());
			int processed = 0;

			List<MstRegistryDetailsPageEntity> toSave = new ArrayList<>();

			for (RegistryRowDTO r : rows) {
				processed++;
				progressStore.set(fileId, (processed * 100) / total);

				if (r.isValid()) {
					try {
						MstRegistryDetailsPageEntity ent = mapper.toEntity(r.getRowData());
						if (ent.getBrnNo() == null || ent.getBrnNo().isBlank()) {
							ent.setBrnNo(generateBrn());
						}
						toSave.add(ent);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// audit fix here (same as you added)
			for (MstRegistryDetailsPageEntity ent : toSave) {
				if (ent.getCreatedDateTime() == null)
					ent.setCreatedDateTime(new Date());
				ent.setUpdatedDateTime(new Date());
				if (ent.getCreatedUserId() == null)
					ent.setCreatedUserId(0L);
				ent.setUpdatedUserId(0L);
				ent.setCreatedIp("localhost");
				ent.setUpdatedIp("localhost");
				ent.setCreatedUserAgent("async-upload");
				ent.setUpdatedUserAgent("async-upload");
				ent.setBrnNo(generateBrn());
				ent.setRegUserId(Integer.parseInt(mstRegistryDetailsPageServiceImpl.getLoginUsernameId().toString()));
//				ent.setLocationCode(locationCode);
			}

			if (!toSave.isEmpty()) {
				repository.saveAll(toSave);
			}

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
					e.setBrnNo(generateBrn());
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

	private String generateBrn() {
		return "BRN-" + UUID.randomUUID().toString();
	}
}
