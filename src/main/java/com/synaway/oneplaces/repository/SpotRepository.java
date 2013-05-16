package com.synaway.oneplaces.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synaway.oneplaces.model.Spot;


public interface SpotRepository  extends JpaRepository<Spot, Long> {

}