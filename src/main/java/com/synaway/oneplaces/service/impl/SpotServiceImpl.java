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
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

@Service
@Transactional
public class SpotServiceImpl implements SpotService {

	private static Logger logger = Logger.getLogger(SpotServiceImpl.class);
	
	@Autowired
	private SpotRepository spotRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
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
		if(spot.getUser() == null){
			spot.setUser(userService.getCurrentUser());
		}
		return spotRepository.save(spot);
	}
	

	@Override
	public List<Spot> getByUser(User user, int limit, int offset){
		return spotRepository.findByUserOrderByTimestampDesc(user, new PageRequest(offset, limit));
	}
	
	@Override
	public Spot json2Spot(String json) throws MissingServletRequestParameterException, JsonProcessingException, IOException, UserException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode ob = mapper.readTree(json);
		Spot spot = new Spot();
		
		
		if(!ob.has("longitude")){
			throw new MissingServletRequestParameterException("longitude", "Double");
		}
		
		if(!ob.has("latitude")){
			throw new MissingServletRequestParameterException("latitude", "Double");
		}		

		User user = null;
		if(ob.has("userId")){
			user = userRepository.findOne(ob.get("userId").asLong());
			if(user == null){
				throw new UserException("User with id "+ob.get("userId").asLong()+" does not exist", UserException.USER_NOT_FOUND);
			}
		}
		
		if(!ob.has("status")){
			throw new MissingServletRequestParameterException("status", "String");
		}
		
		spot.setStatus(ob.get("status").asText());
		spot.setTimestamp(new Date());
		spot.setUser(user);
		spot.setLocation(createPoint(ob.get("longitude").asDouble(), ob.get("latitude").asDouble()));
		if(ob.has("spotId")){
			spot.setId(ob.get("spotId").asLong());
		}
		
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
	
	@Override
	public Spot updateSpot(Spot spot){
		Spot existing = spotRepository.findOne(spot.getId());
		
		
				
		if(spot.getLocation() != null){
			existing.setLocation(spot.getLocation());
		}
		if(spot.getStatus() != null){
			existing.setStatus(spot.getStatus());
		}
		if(spot.getTimestamp() != null){
			existing.setTimestamp(spot.getTimestamp());
		}
		return spotRepository.save(existing);
		
	}
}
