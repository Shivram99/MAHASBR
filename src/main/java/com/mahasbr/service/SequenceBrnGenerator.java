package com.mahasbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mahasbr.repository.BrnSequenceRepository;

@Service
public class SequenceBrnGenerator implements BrnGeneratorService {

    @Autowired
    private BrnSequenceRepository seqRepo;

    @Override
    @Transactional
    public String generateBrn(String stateCode) {

        long nextVal = seqRepo.getNextVal(stateCode);

        // Format SS0000XXXXXXXXXX (16 characters)
        return String.format("%02d0000%010d", Integer.parseInt(stateCode), nextVal);
    }
}