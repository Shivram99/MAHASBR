package com.mahasbr.service;

import java.util.List;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;

import com.mahasbr.config.RegistryFetchRoleProperties;
import com.mahasbr.entity.MstRegistryDetailsPageEntity;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.MstRegistryDetailsPageRepository;
import com.mahasbr.security.context.UserContext;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class RegionStrategy implements RegistryDetailsFetchStrategy {

	private final RegistryFetchRoleProperties roleProperties;
	private final DistrictMasterRepository districtRepo;
	private final MstRegistryDetailsPageRepository repository;

	@Override
	public boolean supports(Set<String> roles) {
		return roles.stream().anyMatch(roleProperties.getRegion()::contains);
	}

	@Override
	public Slice<MstRegistryDetailsPageEntity> fetch(Long cursor, int size, UserContext user) {

		List<String> districts = districtRepo.findDistrictNamesByDivisionCode(user.divisionCode());

		if (districts == null || districts.isEmpty()) {
			return new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
		}

		// 🔹 Fetch one extra row to detect hasNext
		Pageable pageable = PageRequest.of(0, size + 1);

		List<MstRegistryDetailsPageEntity> rows = repository
				.findNextByDistricts(districts.stream().map(String::toLowerCase).toList(), cursor, pageable);

		boolean hasNext = rows.size() > size;

		List<MstRegistryDetailsPageEntity> content = hasNext ? rows.subList(0, size) : rows;

		return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
	}

}
