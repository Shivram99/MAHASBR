package com.mahasbr.service;

//
//@Service
//@RequiredArgsConstructor
public class CachedBrnGeneratorService {

//implements BrnGeneratorService {

//	 private final BrnSequenceRepository seqRepo;
//
//	    @Value("${brn.generator.cache.size:10000}")
//	    private int cacheSize;
//
//	    private final Map<String, SequenceCache> cacheMap = new ConcurrentHashMap<>();
//
//	    @Override
//	    @Transactional
//	    public synchronized String generateBrn(String stateCode) {
//
//	        SequenceCache cache = cacheMap.computeIfAbsent(stateCode, k -> new SequenceCache());
//
//	        if (cache.current >= cache.max) {
//	            long newMax = seqRepo.incrementAndGetMax(stateCode, cacheSize);
//	            cache.max = newMax;
//	            cache.current = newMax - cacheSize + 1;
//	        }
//
//	        long nextVal = cache.current++;
//
//	        return String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
//	    }
//
//	    private static class SequenceCache {
//	        volatile long current = 0;
//	        volatile long max = 0;
//	    }
}
