package com.mahasbr.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.Circular;

@Repository
public interface CircularRepository extends JpaRepository<Circular, Long> {

}
