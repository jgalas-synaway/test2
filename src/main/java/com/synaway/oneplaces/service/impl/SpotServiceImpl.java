package com.synaway.oneplaces.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@Service
@Transactional
public class SpotServiceImpl implements SpotService {

	private static Logger logger = Logger.getLogger(SpotServiceImpl.class);
	
	@Autowired
	SpotRepository spotRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public Spot getSpot(long id){
		return spotRepository.findOne(id);
	}
	
	@Override
	public List<Spot> getAll(){
		return spotRepository.findAll();
	}
	
	@Override
	public Spot saveSpot(Spot spot){
		return spotRepository.save(spot);
	}
	

	@Override
	public List<Spot> getByUser(User user, int limit, int offset){
		return spotRepository.findByUserOrderByTimestampDesc(user, new PageRequest(offset, limit));
	}
	
	@Override
	public Spot json2Spot(String json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode ob = mapper.readTree(json);
		Spot spot = new Spot();
		
		
		if(!ob.has("longitude")){
			//TODO throw custom exception
			throw new Exception("Missing argument longitude");
		}
		
		if(!ob.has("latitude")){
			//TODO throw custom exception
			throw new Exception("Missing argument latitude");
		}		
		
		if(!ob.has("userId")){
			//TODO throw custom exception
			throw new Exception("Missing argument userId");
		}
		
		User user = userRepository.findOne(ob.get("userId").asLong());
		if(user == null){
			//TODO throw custom exception
			throw new Exception("User with id "+ob.get("userId").asLong()+" does not exist");
		}
		
		if(!ob.has("status")){
			//TODO throw custom exception
			throw new Exception("Missing argument status");
		}
		
		spot.setStatus(ob.get("status").asText());
		spot.setTimestamp(new Date());
		spot.setUser(user);
		spot.setLocation(createPoint(ob.get("longitude").asDouble(), ob.get("latitude").asDouble()));
		
		
		return spot;		 
	}
	
	@Override
	public Point createPoint(double x, double y){
		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);		
        Coordinate coord = new Coordinate( x, y );
        Point point = gf.createPoint( coord );
		return point;
	}

	@Override
	public List<Spot> getByLatitudeLongitudeAndRadius(Double latitude, Double longitude, Integer radius) {
		String point = "POINT("+longitude+" "+latitude+")";
		List<Spot> spots = spotRepository.findByLatitudeLongitudeAndRadius(point, radius);
		return spots;
	}
}
