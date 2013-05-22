package com.synaway.oneplaces.service;

import java.util.List;

import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;

public interface UserLocationService {

	List<UserLocation> getByUser(User user, int limit, int offset);

}
