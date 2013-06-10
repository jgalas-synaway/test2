package com.synaway.oneplaces.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;


public interface SpotRepository  extends JpaRepository<Spot, Long> {

	List<Spot> findByUserOrderByTimestampDesc(User user, Pageable pageable);
	
	@Query(nativeQuery=true, value="SELECT * FROM spot WHERE created_at > now() - INTERVAL '9 minute' AND status <> 'occupied' AND ST_Distance_Sphere(location, ST_GeometryFromText(?1)) < ?2")
	List<Spot> findByLatitudeLongitudeAndRadius(String point, Integer radius);
	
	List<Spot> findByUser(User user);

}