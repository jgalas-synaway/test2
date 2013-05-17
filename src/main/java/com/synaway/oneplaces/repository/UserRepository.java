package com.synaway.oneplaces.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synaway.oneplaces.model.User;


public interface UserRepository  extends JpaRepository<User, Long> {
	
	public User findOneByLogin(String login); 
	
}