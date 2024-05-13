package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.VillageMaster;

@Repository
public interface VillageMasterRepository extends JpaRepository<VillageMaster, Long> {

}
