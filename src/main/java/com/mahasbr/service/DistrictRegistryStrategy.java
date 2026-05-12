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
@Order(3)
@RequiredArgsConstructor
public class DistrictRegistryStrategy implements RegistryDetailsFetchStrategy {

	private final RegistryFetchRoleProperties roleProperties;

	private final MstRegistryDetailsPageRepository mstRegistryDetailsPageRepository;

	private final DistrictMasterRepository districtMasterRepository;

	@Override
	public boolean supports(Set<String> roles) {
		return roles.stream().anyMatch(roleProperties.getDistrict()::contains);
	}

//    @Override
//    public Slice<MstRegistryDetailsPageEntity> fetch(Pageable pageable, UserContext user) {
//    	   Optional<String> district =
//    			   districtMasterRepository.findDistrictNameById(user.districtId());
//
//           return district
//                   .map(d -> mstRegistryDetailsPageRepository.findByDistrictName(d, pageable))
//                   .orElse(Page.empty(pageable));
//       }

	@Override
	public Slice<MstRegistryDetailsPageEntity> fetch(Long cursor, int size, UserContext user) {

		Pageable pageable = PageRequest.of(0, size);

		return districtMasterRepository.findDistrictNameById(user.districtId())
				.map(districtName -> mstRegistryDetailsPageRepository.findNextByDistrict(districtName.toLowerCase(), 
						cursor, pageable))
				.orElseGet(() -> new SliceImpl<>(List.of(), pageable, false));
	}

}
