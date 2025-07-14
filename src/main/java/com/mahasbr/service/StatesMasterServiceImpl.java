package com.mahasbr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mahasbr.entity.StatesMaster;
import com.mahasbr.repository.StatesMasterRepository;

@Service
public class StatesMasterServiceImpl implements StatesMasterService {
	@Autowired
	StatesMasterRepository statesMasterRepository;

	@Override
	public StatesMaster findByStateName(String stateName) {
		
		Optional<StatesMaster> state=statesMasterRepository.findByStateName(stateName);
		if(state.isPresent())
		return state.get();
		
		return null;

	}
}
