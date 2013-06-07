package com.synaway.oneplaces.service;

import java.util.List;

import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;


/**
 * This service is created for support user locations functionality.
 * It's used for manipulations on locations visited by user.
 * 
 * @author Łukasz Kracoń
 *
 */
public interface UserLocationService {

	
	/**
	 * Returns locations of places visited by user ordered by timestamp
	 * 
	 * @param user - user for which locations should be retrieved 
	 * @param limit - limit of items in list
	 * @param offset
	 * @return
	 */
	List<UserLocation> getByUser(User user, int limit, int offset);

	/**
	 * Get list of locations that were visited less than 1 min ago. One location per user.
	 * 
	 * @return
	 */
	List<UserLocation> getActive();

}
