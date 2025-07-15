package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.Village;
import com.mahasbr.repository.VillageRepository;

@Service
public class VillageService {

	@Autowired
    private VillageRepository villageRepository;

    public List<Village> getAllVillages() {
        return villageRepository.findAll();
    }

    public Village getVillageById(Long id) {
        return villageRepository.findById(id).orElseThrow(() -> new RuntimeException("Village not found"));
    }

    public Village createVillage(Village village) {
        return villageRepository.save(village);
    }

    public void deleteVillage(Long id) {
        villageRepository.deleteById(id);
    }

    public List<Village> getVillagesByTalukaId(Long talukaId) {
        return villageRepository.findByTalukaId(talukaId);
    }
}
