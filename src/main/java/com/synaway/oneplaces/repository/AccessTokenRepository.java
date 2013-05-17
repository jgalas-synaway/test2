package com.synaway.oneplaces.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.User;


public interface AccessTokenRepository  extends JpaRepository<AccessToken, Long> {

	public AccessToken findByUserOrderByExpireDesc(User user);
}