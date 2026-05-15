package com.mahasbr.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mahasbr.dto.UserDto;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.DivisionMaster;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.DivisionRepository;
import com.mahasbr.repository.RegistryMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleLocationPolicyService {

    private static final Set<String> REGISTRY_REQUIRED_ROLES = Set.of("ROLE_REG_AUTH_API", "ROLE_REG_AUTH_CSV");
    private static final Set<String> DIVISION_REQUIRED_ROLES = Set.of("ROLE_DES_REGION", "ROLE_DES_DISTRICT");
    private static final Set<String> DISTRICT_REQUIRED_ROLES = Set.of("ROLE_DES_DISTRICT");

    private final RegistryMasterRepository registryRepository;
    private final DivisionRepository divisionRepository;
    private final DistrictMasterRepository districtRepository;

    public ValidatedUserLocation validateAndResolve(UserDto dto) {
        LocationRules rules = resolveRules(dto.getRoles());

        RegistryMasterEntity registry = rules.requiresRegistry()
                ? resolveRegistry(dto.getRegistryId())
                : null;
        String divisionCode = rules.requiresDivision()
                ? resolveDivisionCode(dto)
                : null;
        DistrictMaster district = rules.requiresDistrict()
                ? resolveDistrict(dto.getDistrictId())
                : null;

        if (rules.requiresDivision() && rules.requiresDistrict() && district != null
                && !district.getDivisionCode().equalsIgnoreCase(divisionCode)) {
            throw new IllegalArgumentException("Selected district does not belong to the selected division.");
        }

        return new ValidatedUserLocation(registry, divisionCode, district);
    }

    private LocationRules resolveRules(Set<String> roles) {
        Set<String> normalizedRoles = normalizeRoles(roles);

        return new LocationRules(
                normalizedRoles.stream().anyMatch(REGISTRY_REQUIRED_ROLES::contains),
                normalizedRoles.stream().anyMatch(DIVISION_REQUIRED_ROLES::contains),
                normalizedRoles.stream().anyMatch(DISTRICT_REQUIRED_ROLES::contains));
    }

    private Set<String> normalizeRoles(Set<String> roles) {
        return roles == null
                ? Set.of()
                : roles.stream()
                        .filter(StringUtils::hasText)
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .collect(Collectors.toSet());
    }

    private RegistryMasterEntity resolveRegistry(Long registryId) {
        if (registryId == null) {
            throw new IllegalArgumentException("Registry is required for the selected role(s).");
        }

        return registryRepository.findById(registryId)
                .orElseThrow(() -> new IllegalArgumentException("Selected registry is invalid."));
    }

    private String resolveDivisionCode(UserDto dto) {
        if (StringUtils.hasText(dto.getDivisionCode())) {
            String divisionCode = dto.getDivisionCode().trim();

            if (divisionRepository.findByDivisionCode(divisionCode).isEmpty()) {
                throw new IllegalArgumentException("Selected division is invalid.");
            }

            return divisionCode;
        }

        if (dto.getDivisionId() == null) {
            throw new IllegalArgumentException("Division is required for the selected role(s).");
        }

        DivisionMaster division = divisionRepository.findById(dto.getDivisionId())
                .orElseThrow(() -> new IllegalArgumentException("Selected division is invalid."));

        return division.getDivisionCode();
    }

    private DistrictMaster resolveDistrict(Long districtId) {
        if (districtId == null) {
            throw new IllegalArgumentException("District is required for the selected role(s).");
        }

        return districtRepository.findById(districtId)
                .orElseThrow(() -> new IllegalArgumentException("Selected district is invalid."));
    }

    public record ValidatedUserLocation(
            RegistryMasterEntity registry,
            String divisionCode,
            DistrictMaster district) {
    }

    private record LocationRules(
            boolean requiresRegistry,
            boolean requiresDivision,
            boolean requiresDistrict) {
    }
}
