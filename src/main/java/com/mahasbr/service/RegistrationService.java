package com.mahasbr.service;

import com.mahasbr.dto.RegistrationStatsDTO;
import com.mahasbr.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository repository;

    public List<RegistrationStatsDTO> getRegistrationStats() {
        List<Object[]> results = repository.getRegistrationStatsRaw();

        return results.stream()
                .map(r -> new RegistrationStatsDTO(
                        (String) r[0],
                        ((Number) r[1]).intValue(),
                        (String) r[2],
                        ((Number) r[3]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
