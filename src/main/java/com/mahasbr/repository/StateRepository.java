package com.mahasbr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahasbr.entity.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {}