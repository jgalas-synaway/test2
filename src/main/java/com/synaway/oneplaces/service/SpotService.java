package com.synaway.oneplaces.service;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonProcessingException;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.vividsolutions.jts.geom.Point;

public interface SpotService {

	Spot getSpot(long id);
	
	List<Spot> getAll();

	Spot json2Spot(String json) throws JsonProcessingException, IOException, Exception;

	Point createPoint(double x, double y);

	Spot saveSpot(Spot spot);

	List<Spot> getByLatitudeLongitudeAndRadius(Double latitude, Double longitude, Integer radius);

	List<Spot> getByUser(User user, int limit, int offset);

}
