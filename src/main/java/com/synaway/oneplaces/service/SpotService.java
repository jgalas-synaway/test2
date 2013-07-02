package com.synaway.oneplaces.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.MissingServletRequestParameterException;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.vividsolutions.jts.geom.Point;

/**
 * This service is created for support spots functionality.
 * It's used for manipulations on spot data in application.
 * 
 * @author Łukasz Kracoń
 *
 */
public interface SpotService {

	/**
	 * Method used to find spot with specified id
	 * 
	 * @param id - user id
	 * @return Spot or null if not exist
	 */
	Spot getSpot(long id);
	
	/**
	 * Returns list of all spots in application
	 * 
	 * @return if there is no spot, returns empty list
	 */
	List<Spot> getAll();

	
	/**
	 * Converts JSON string into Spot object.
	 * 
	 * @param json - string representing Spot 
	 * <pre>
	 *	{
	 *		"userId":1, 
	 *		"longitude":19.846278, 
	 *		"latitude": 50.93063, 
	 *		"status":"occupied"
	 *	}
	 *</pre>
	 * @return 
	 * @throws JsonProcessingException
	 * @throws IOException
	 * @throws MissingServletRequestParameterException - if some arguments are missing
	 * @throws UserException - if there is no user with specified id, code 101.
	 */
	Spot json2Spot(String json) throws IOException, MissingServletRequestParameterException, UserException;

	
	/** 
	 * creates Point based on x and y. 
	 * @param x
	 * @param y
	 * @return
	 */
	Point createPoint(double x, double y);

	/**
	 * Saves or if id is not present updates spot in database
	 * 
	 * @param spot
	 * @return
	 */
	Spot saveSpot(Spot spot);

	
	/**
	 * Returns all spots which are closer than specified distance from point
	 * 
	 * @param latitude - latitude of point
	 * @param longitude - longitude of point
	 * @param radius - distance in meters
	 * @return
	 */
	List<Spot> getByLatitudeLongitudeAndRadius(Double latitude, Double longitude, Integer radius);

	
	/**
	 * Returns spots by user paginated
	 * 
	 * @param user
	 * @param limit
	 * @param offset
	 * @return
	 */
	List<Spot> getByUser(User user, int limit, int offset);

	Spot updateSpot(Spot spot);


	/**
	 * Returns spots paginated and ordered
	 * 
	 * @param order
	 * @param asc
	 * @param startIndex
	 * @param limit
	 * @return
	 */
	List<Spot> getAll(String order, String asc, long startIndex, long limit);


	/**
	 * Returns number of spots which are closer than specified distance from point
	 * 
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	Long countByLatitudeLongitudeAndRadiusTtl3(Double latitude, Double longitude,
			Integer radius);
	
	/**
	 * Returns number of spots which are closer than specified distance from point
	 * 
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	Long countByLatitudeLongitudeAndRadiusTtl6(Double latitude, Double longitude,
			Integer radius);
	
	/**
	 * Returns number of spots which are closer than specified distance from point
	 * 
	 * @param latitude
	 * @param longitude
	 * @param radius
	 * @return
	 */
	Long countByLatitudeLongitudeAndRadiusTtl9(Double latitude, Double longitude,
			Integer radius);



}
