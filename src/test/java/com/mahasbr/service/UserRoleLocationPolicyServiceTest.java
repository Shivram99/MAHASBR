package com.mahasbr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mahasbr.dto.UserDto;
import com.mahasbr.entity.DistrictMaster;
import com.mahasbr.entity.DivisionMaster;
import com.mahasbr.entity.RegistryMasterEntity;
import com.mahasbr.repository.DistrictMasterRepository;
import com.mahasbr.repository.DivisionRepository;
import com.mahasbr.repository.RegistryMasterRepository;

@ExtendWith(MockitoExtension.class)
class UserRoleLocationPolicyServiceTest {

    @Mock
    private RegistryMasterRepository registryRepository;

    @Mock
    private DivisionRepository divisionRepository;

    @Mock
    private DistrictMasterRepository districtRepository;

    private UserRoleLocationPolicyService service;

    @BeforeEach
    void setUp() {
        service = new UserRoleLocationPolicyService(registryRepository, divisionRepository, districtRepository);
    }

    @Test
    void shouldRequireRegistryForRegistryRoles() {
        UserDto dto = new UserDto();
        dto.setRoles(Set.of("ROLE_REG_AUTH_API"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateAndResolve(dto));

        assertEquals("Registry is required for the selected role(s).", exception.getMessage());
    }

    @Test
    void shouldRequireDivisionAndDistrictForDistrictRole() {
        UserDto dto = new UserDto();
        dto.setRoles(Set.of("ROLE_DES_DISTRICT"));
        dto.setDivisionCode("PUNE");
        dto.setDistrictId(10L);

        DivisionMaster division = DivisionMaster.builder().divisionId(3L).divisionCode("PUNE").divisionName("Pune").isActive(true)
                .build();
        DistrictMaster district = new DistrictMaster();
        district.setDistrictId(10L);
        district.setDivisionCode("PUNE");

        when(divisionRepository.findByDivisionCode("PUNE")).thenReturn(Optional.of(division));
        when(districtRepository.findById(10L)).thenReturn(Optional.of(district));

        UserRoleLocationPolicyService.ValidatedUserLocation location = service.validateAndResolve(dto);

        assertEquals("PUNE", location.divisionCode());
        assertEquals(district, location.district());
        assertNull(location.registry());
    }

    @Test
    void shouldClearIrrelevantLocationValuesForAdminRole() {
        UserDto dto = new UserDto();
        dto.setRoles(Set.of("ROLE_ADMIN"));
        dto.setRegistryId(1L);
        dto.setDivisionCode("PUNE");
        dto.setDistrictId(99L);

        UserRoleLocationPolicyService.ValidatedUserLocation location = service.validateAndResolve(dto);

        assertNull(location.registry());
        assertNull(location.divisionCode());
        assertNull(location.district());
    }

    @Test
    void shouldRejectDistrictWhenItDoesNotMatchSelectedDivision() {
        UserDto dto = new UserDto();
        dto.setRoles(Set.of("ROLE_DES_DISTRICT"));
        dto.setDivisionCode("NASHIK");
        dto.setDistrictId(77L);

        DivisionMaster division = DivisionMaster.builder().divisionId(2L).divisionCode("NASHIK").divisionName("Nashik").isActive(true)
                .build();
        DistrictMaster district = new DistrictMaster();
        district.setDistrictId(77L);
        district.setDivisionCode("PUNE");

        when(divisionRepository.findByDivisionCode("NASHIK")).thenReturn(Optional.of(division));
        when(districtRepository.findById(77L)).thenReturn(Optional.of(district));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.validateAndResolve(dto));

        assertEquals("Selected district does not belong to the selected division.", exception.getMessage());
    }
}
