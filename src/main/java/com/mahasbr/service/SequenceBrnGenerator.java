package com.mahasbr.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.repository.BrnSequenceRepository;

@Service
public class SequenceBrnGenerator implements BrnGeneratorService {

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Value("${brn.generator.cache.size:10000}")
//    private int cacheSize;
//
////    @Transactional
//    public String generateBrn(String stateCode) {
//        String sequenceName = "brn_seq_" + stateCode;
//        Long nextVal = ((Number) entityManager
//                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM dual")
//                .getSingleResult()).longValue();
//
//        // Format: SS0000XXXXXXXXXX (16-digit)
//        String formatted = String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
//        return formatted;
//    }

	@Autowired
	private BrnSequenceRepository seqRepo;

	@Value("${brn.generator.cache.size:10000}")
	private int cacheSize;

	private final Map<String, SequenceCache> cacheMap = new ConcurrentHashMap<>();

	@Override
	@Transactional
	public synchronized String generateBrn(String stateCode) {

		SequenceCache cache = cacheMap.computeIfAbsent(stateCode, k -> new SequenceCache());

		if (cache.current >= cache.max) {
			long newMax = seqRepo.incrementAndGetMax(stateCode, cacheSize);
			cache.max = newMax;
			cache.current = newMax - cacheSize + 1;
		}

		long nextVal = cache.current++;

		return String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
	}

	private static class SequenceCache {
		volatile long current = 0;
		volatile long max = 0;
	}
}
