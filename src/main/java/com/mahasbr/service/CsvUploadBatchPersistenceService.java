package com.mahasbr.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mahasbr.dto.CsvUploadFailedRecordDto;
import com.mahasbr.dto.CsvUploadParsedRecordDto;
import com.mahasbr.entity.CsvUploadFailedRecordEntity;
import com.mahasbr.entity.CsvUploadSuccessRecordEntity;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryFailedEntity;
import com.mahasbr.mapper.CsvUploadMapper;
import com.mahasbr.repository.CsvUploadFailedRecordRepository;
import com.mahasbr.repository.CsvUploadSuccessRecordRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.MstRegistryFailedRepository;
import com.mahasbr.validator.CsvUploadValidator;
import com.mahasbr.util.LocationGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvUploadBatchPersistenceService {

	private final CsvUploadMapper mapper;
	private final MstRegistryDetailsPageRepository registryRepository;
	private final CsvUploadSuccessRecordRepository successRecordRepository;
	private final CsvUploadFailedRecordRepository failedRecordRepository;
	private final MstRegistryFailedRepository mstRegistryFailedRepository;
	private final BrnGeneratorService brnGeneratorService;
	private final LocationGenerator locationGenerator;
	private final ObjectMapper objectMapper;

	public CsvUploadBatchPersistenceResult persistBatch(String jobId, List<CsvUploadParsedRecordDto> batch, Integer regUserId,
			String fileName) {
		if (batch.isEmpty()) {
			return new CsvUploadBatchPersistenceResult(0, 0);
		}

		try {
			return persistBatchTransactional(jobId, batch, regUserId);
		} catch (Exception exception) {
			log.warn("Batch insert failed for jobId={}, batchSize={}. Falling back to row-by-row persistence.", jobId,
					batch.size(), exception);
			return persistRowByRow(jobId, batch, regUserId, fileName);
		}
	}

	protected CsvUploadBatchPersistenceResult persistBatchTransactional(String jobId, List<CsvUploadParsedRecordDto> batch,
			Integer regUserId) {
		List<MstRegistryDetailsPageEntity> entities = new ArrayList<>(batch.size());
		List<CsvUploadSuccessRecordEntity> successRecords = new ArrayList<>(batch.size());

		for (CsvUploadParsedRecordDto parsedRecord : batch) {
			MstRegistryDetailsPageEntity entity = mapEntity(parsedRecord, regUserId);
			entities.add(entity);
			successRecords.add(CsvUploadSuccessRecordEntity.builder().jobId(jobId).rowNumber(parsedRecord.getRowNumber())
					.brn(entity.getBrnNo()).establishmentName(entity.getNameOfEstablishmentOrOwner())
					.rawData(rawData(parsedRecord)).build());
		}

		registryRepository.saveAll(entities);
		registryRepository.flush();
		successRecordRepository.saveAll(successRecords);
		successRecordRepository.flush();
		return new CsvUploadBatchPersistenceResult(successRecords.size(), 0);
	}

	private CsvUploadBatchPersistenceResult persistRowByRow(String jobId, List<CsvUploadParsedRecordDto> batch, Integer regUserId,
			String fileName) {
		int successCount = 0;
		int failedCount = 0;

		for (CsvUploadParsedRecordDto parsedRecord : batch) {
			try {
				MstRegistryDetailsPageEntity entity = mapEntity(parsedRecord, regUserId);
				MstRegistryDetailsPageEntity savedEntity = registryRepository.saveAndFlush(entity);
				successRecordRepository.save(CsvUploadSuccessRecordEntity.builder().jobId(jobId)
						.rowNumber(parsedRecord.getRowNumber()).brn(savedEntity.getBrnNo())
						.establishmentName(savedEntity.getNameOfEstablishmentOrOwner()).rawData(rawData(parsedRecord)).build());
				successCount++;
			} catch (Exception exception) {
				failedCount++;
				saveFailedRecord(jobId, parsedRecord, null, resolveMessage(exception), fileName);
			}
		}

		return new CsvUploadBatchPersistenceResult(successCount, failedCount);
	}

	public void saveFailedRecord(String jobId, CsvUploadParsedRecordDto parsedRecord, String brn, String reason, String fileName) {
		String establishmentName = parsedRecord.getRowData().getOrDefault(CsvUploadValidator.ESTABLISHMENT_NAME, "");
		failedRecordRepository.save(CsvUploadFailedRecordEntity.builder().jobId(jobId).rowNumber(parsedRecord.getRowNumber())
				.establishmentName(establishmentName).brn(brn).errorMessage(reason).rawData(rawData(parsedRecord)).build());
		saveMstFailedRecord(parsedRecord, reason, fileName);
	}

	public void savePreviewFailedRecords(String jobId, List<CsvUploadFailedRecordDto> failedRecords) {
		if (failedRecords.isEmpty()) {
			return;
		}

		List<CsvUploadFailedRecordEntity> entities = failedRecords.stream()
				.map(record -> CsvUploadFailedRecordEntity.builder().jobId(jobId).rowNumber(record.getRowNumber())
						.establishmentName(record.getEstablishmentName()).brn(record.getBrn())
						.errorMessage(record.getErrorReason()).rawData(record.getRawData()).build())
				.toList();
		failedRecordRepository.saveAll(entities);
		failedRecordRepository.flush();
	}

	private MstRegistryDetailsPageEntity mapEntity(CsvUploadParsedRecordDto parsedRecord, Integer regUserId) {
		MstRegistryDetailsPageEntity entity = mapper.toEntity(parsedRecord.getRowData());
		entity.setBrnNo(generateUniqueBrn());
		entity.setRegUserId(regUserId);
		String locationCode = locationGenerator.getLocationCode(entity.getDistrict(), entity.getTaluka(),
				entity.getTownVillage());
		entity.setLocationCode("NA".equalsIgnoreCase(locationCode) ? null : locationCode);
		return entity;
	}

	private String generateUniqueBrn() {
		String brn;
		do {
			brn = brnGeneratorService.generateBrn("27");
		} while (registryRepository.existsByBrnNo(brn));
		return brn;
	}

	private void saveMstFailedRecord(CsvUploadParsedRecordDto parsedRecord, String reason, String fileName) {
		try {
			MstRegistryFailedEntity failedEntity = new MstRegistryFailedEntity();
			failedEntity.setApiName(fileName);
			failedEntity.setApiUrl("csv-upload/process");
			failedEntity.setErrorMessage(reason);
			failedEntity.setStateName("Maharashtra");
			failedEntity.setDistrictName(parsedRecord.getRowData().getOrDefault(CsvUploadValidator.DISTRICT, ""));
			failedEntity.setTalukaName(parsedRecord.getRowData().getOrDefault(CsvUploadValidator.TALUKA, ""));
			failedEntity.setVillageName(parsedRecord.getRowData().getOrDefault(CsvUploadValidator.TOWN_VILLAGE, ""));
			mstRegistryFailedRepository.save(failedEntity);
		} catch (Exception exception) {
			log.debug("Unable to persist mst_registry_failed record for rowNumber={}.", parsedRecord.getRowNumber(),
					exception);
		}
	}

	private String resolveMessage(Exception exception) {
		return exception.getMessage() == null || exception.getMessage().isBlank() ? "Failed to save record"
				: exception.getMessage();
	}

	private String rawData(CsvUploadParsedRecordDto parsedRecord) {
		try {
			return objectMapper.writeValueAsString(parsedRecord.getRowData());
		} catch (JsonProcessingException exception) {
			return parsedRecord.getRowData().toString();
		}
	}
}
