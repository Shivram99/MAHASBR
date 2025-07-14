package com.mahasbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.State;
import com.mahasbr.repository.StateRepository;

@Service
public class StateService {
    
    @Autowired
    private StateRepository stateRepository;

    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    public State getStateById(Long id) {
        return stateRepository.findById(id).orElseThrow(() -> new RuntimeException("State not found"));
    }

    public State createState(State state) {
        return stateRepository.save(state);
    }

    public void deleteState(Long id) {
        stateRepository.deleteById(id);
    }
}
