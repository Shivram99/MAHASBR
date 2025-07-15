package com.mahasbr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.StatesMaster;

@Repository
public interface StatesMasterRepository extends JpaRepository<StatesMaster, Long> {
	
	Optional<StatesMaster> findByCensusStateCode(String censusCode);
    boolean existsByCensusStateCode(String censusCode);

	Optional<StatesMaster> findByStateName(String stateName);
	/* Optional<StatesMaster> FindByStateName(String stateName); */

}
