package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.MasterNicDetails;

@Repository
public interface MasterNicDetailsRepository extends JpaRepository<MasterNicDetails, Long> {

}
