package com.synaway.oneplaces.repository;

import java.util.Date;
import java.util.List;

import org.hibernate.type.TrueFalseType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;


public interface UserLocationRepository  extends JpaRepository<UserLocation, Long> {

	List<UserLocation> findByUserOrderByTimestampDesc(User user, Pageable pageable);
	
	List<UserLocation> findByUserAndTimestampBetweenOrderByTimestampDesc(User user, Date start, Date end, Pageable pageable);
	
	@Query(nativeQuery=true ,value="select DISTINCT ON (user_id) * from user_location u where u.timestamp > now() - INTERVAL '1 minute' group by u.user_id,  u.id order by u.user_id, u.timestamp DESC")
	List<UserLocation> findActive();
	
	public List<UserLocation> findByUser(User user);

}