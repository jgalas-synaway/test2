package com.synaway.oneplaces.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;


public interface AccessTokenRepository  extends JpaRepository<AccessToken, Long> {

	List<AccessToken> findByUserOrderByExpireDesc(User user);
	
	AccessToken findByToken(String token);
	
	List<AccessToken> findByUser(User user);
}