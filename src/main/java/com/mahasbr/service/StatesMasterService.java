package com.mahasbr.service;

import com.mahasbr.entity.StatesMaster;

public interface StatesMasterService {
	
	public StatesMaster findByStateName(String stateName);

}
