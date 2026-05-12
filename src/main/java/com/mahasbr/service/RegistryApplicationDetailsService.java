package com.mahasbr.service;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.security.context.UserContext;
import com.mahasbr.util.SliceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistryApplicationDetailsService {

	private final List<RegistryDetailsFetchStrategy> strategies;
	private final CurrentUserService currentUserService;

	/**
	 * 🔹 CACHED ENTRY POINT (Redis / Caffeine) 🔹 Returns Object to avoid Redis
	 * generic casting issues 🔹 Cache ONLY first page (cursor == null)
	 */
	public Object fetchRegistriesDetailsCached(Long cursor, int size) {

		return fetchRegistriesDetailsInternal(cursor, size);
	}

	/**
	 * 🔹 INTERNAL METHOD (NO CACHE) 🔹 Always hits DB 🔹 Returns strongly typed
	 * SliceResponse
	 */
	@Transactional(readOnly = true)
	public SliceResponse<MstRegistryDetailsPageEntity> fetchRegistriesDetailsInternal(
	        Long cursor,
	        int size) {

	    UserContext user = currentUserService.getCurrentUser();

	    RegistryDetailsFetchStrategy strategy = strategies.stream()
	            .filter(s -> s.supports(user.roles()))
	            .findFirst()
	            .orElseThrow(() -> new IllegalStateException(
	                    "No RegistryDetailsFetchStrategy found for roles: " + user.roles()));

	    // 🔹 Log cache-miss / DB-hit (this method is ONLY called on cache miss)
	    log.warn(
	        "[DB] HIT registryData strategy={} userId={} roles={} cursor={} size={}",
	        strategy.getClass().getSimpleName(),
	        user.userId(),
	        user.roles(),
	        cursor,
	        size
	    );

	    // 🔹 Delegate to strategy (strategy already does size+1 logic)
	    Slice<MstRegistryDetailsPageEntity> slice =
	            strategy.fetch(cursor, size, user);

	    // 🔹 Convert to DTO-safe response (NO Spring Slice leakage)
	    return new SliceResponse<>(
	            List.copyOf(slice.getContent()), // immutable + concrete list
	            slice.hasNext()
	    );
	}

}
