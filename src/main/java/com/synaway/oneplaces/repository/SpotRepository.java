package com.synaway.oneplaces.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.synaway.oneplaces.model.Spot;


public interface SpotRepository  extends JpaRepository<Spot, Long> {

	@Query(nativeQuery=true, value="SELECT * FROM spot WHERE ST_Distance_Sphere(location, ST_GeometryFromText(?1)) < ?2")
	List<Spot> findByLatitudeLongitudeAndRadius(String point, Integer radius);

}