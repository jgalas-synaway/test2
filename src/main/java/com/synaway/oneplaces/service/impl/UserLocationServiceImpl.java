package com.synaway.oneplaces.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.service.UserLocationService;

@Service
@Transactional
public class UserLocationServiceImpl implements UserLocationService {

	@Autowired
	private UserLocationRepository userLocationRepository;
	
	@Override
	public List<UserLocation> getByUser(User user, int limit, int offset){
		return userLocationRepository.findByUserOrderByTimestampDesc(user, new PageRequest(offset, limit));
	}
	
	@Override
	public List<UserLocation> getActive(){
		return userLocationRepository.findActive();
	}
	
}
