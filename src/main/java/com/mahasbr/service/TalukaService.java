package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.Taluka;
import com.mahasbr.repository.TalukaRepository;

@Service
public class TalukaService {

	@Autowired
    private TalukaRepository talukaRepository;

    public List<Taluka> getAllTalukas() {
        return talukaRepository.findAll();
    }

    public Taluka getTalukaById(Long id) {
        return talukaRepository.findById(id).orElseThrow(() -> new RuntimeException("Taluka not found"));
    }

    public Taluka createTaluka(Taluka taluka) {
        return talukaRepository.save(taluka);
    }

    public void deleteTaluka(Long id) {
        talukaRepository.deleteById(id);
    }

    public List<Taluka> getTalukasByDistrictId(Long districtId) {
        return talukaRepository.findByDistrictId(districtId);
    }
}
