package com.mahasbr.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.dto.MstRegistryDetailsPagesDTO;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.entity.MstRegistryFailedEntity;
import com.mahasbr.mapper.MstRegistryDetailsMapper;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.repository.MstRegistryFailedRepository;
import com.mahasbr.util.LocationGenerator;
import com.mahasbr.util.RegistryRecordKey;
import com.mahasbr.util.RegistryRecordSanitizer;

import lombok.RequiredArgsConstructor;

/**
 * Saves records received from a registry API. Kept in its own bean (not inside the sync
 * service) so that the per-record REQUIRES_NEW transactions actually apply: one bad record
 * is rolled back alone and never takes the rest of the batch with it.
 */
@Service
@RequiredArgsConstructor
public class RegistryRecordUpsertService {

	private static final Logger log = LoggerFactory.getLogger(RegistryRecordUpsertService.class);
	private static final String MAHARASHTRA_STATE_CODE = "27";
	public static final int BACKFILL_BATCH_SIZE = 500;

	public enum Outcome { CREATED, UPDATED }

	private final MstRegistryDetailsPageRepository repository;
	private final MstRegistryFailedRepository failedRepository;
	private final MstRegistryDetailsMapper mapper;
	private final BrnGeneratorService brnGeneratorService;
	private final LocationGenerator locationGenerator;

	/**
	 * Inserts a new establishment, or updates the one an earlier sync created.
	 * A BRN is generated only for genuinely new establishments.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Outcome upsert(MstRegistryDetailsPagesDTO dto, Integer regUserId) {
		String key = RegistryRecordKey.of(dto);
		String locationCode = locationGenerator.getLocationCode(dto.getDistrict(), dto.getTaluka(),
				dto.getTownVillage());

		List<MstRegistryDetailsPageEntity> existing = repository.findByRegUserIdAndSourceRecordKey(regUserId, key);
		if (!existing.isEmpty()) {
			if (existing.size() > 1) {
				log.warn("{} rows share source key {} for registry {}; updating the oldest", existing.size(), key,
						regUserId);
			}
			MstRegistryDetailsPageEntity entity = existing.get(0);
			mapper.updateEntityFromDto(dto, entity);
			RegistryRecordSanitizer.sanitize(entity);
			entity.setLocationCode(locationCode);
			repository.save(entity);
			return Outcome.UPDATED;
		}

		MstRegistryDetailsPageEntity entity = mapper.dtoToEntity(dto);
		RegistryRecordSanitizer.sanitize(entity);
		entity.setRegUserId(regUserId);
		entity.setSourceRecordKey(key);
		entity.setBrnNo(brnGeneratorService.generateBrn(MAHARASHTRA_STATE_CODE));
		entity.setLocationCode(locationCode);
		repository.save(entity);
		return Outcome.CREATED;
	}

	/**
	 * Keeps a record that could not be saved, with the reason and its raw JSON, for review.
	 *
	 * @param dto     the parsed record, or null if the JSON itself could not be read
	 * @param rawJson the record exactly as received
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveFailed(MstRegistryDetailsPagesDTO dto, String rawJson, Integer regUserId, String reason,
			String apiName, String apiUrl) {
		try {
			MstRegistryFailedEntity failed = new MstRegistryFailedEntity();
			failed.setErrorMessage(truncate(reason, 255));
			failed.setApiName(truncate(apiName, 255));
			failed.setApiUrl(truncate(apiUrl, 255));
			failed.setRegUserId(regUserId);
			if (dto != null) {
				failed.setDistrictName(truncate(dto.getDistrict(), 255));
				failed.setTalukaName(truncate(dto.getTaluka(), 255));
				failed.setVillageName(truncate(dto.getTownVillage(), 255));
			}
			failed.setRawData(truncate(rawJson, 4000));
			failed.setCreatedAt(LocalDateTime.now());
			failedRepository.save(failed);
		} catch (Exception ex) {
			log.error("Could not save failed registry record ({}): {}", apiName, ex.getMessage(), ex);
		}
	}

	/**
	 * Gives up to one batch of rows saved before the dedupe key existed (earlier API runs, CSV
	 * uploads) their key, so the next sync updates them instead of inserting duplicates.
	 * The caller loops until this returns less than {@link #BACKFILL_BATCH_SIZE}; it must be
	 * called from another bean so the transaction applies.
	 *
	 * @return number of rows updated in this batch
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public int backfillBatch(Integer regUserId) {
		// Always page 0: rows drop out of the "key is null" filter as soon as they are updated.
		Slice<MstRegistryDetailsPageEntity> batch = repository.findByRegUserIdAndSourceRecordKeyIsNull(regUserId,
				PageRequest.of(0, BACKFILL_BATCH_SIZE));
		batch.forEach(row -> row.setSourceRecordKey(RegistryRecordKey.of(row)));
		repository.saveAll(batch.getContent());
		return batch.getNumberOfElements();
	}

	private static String truncate(String value, int max) {
		return value == null || value.length() <= max ? value : value.substring(0, max);
	}
}
