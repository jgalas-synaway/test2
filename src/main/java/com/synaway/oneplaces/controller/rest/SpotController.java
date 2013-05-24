package com.synaway.oneplaces.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;


@Transactional
@Controller
@RequestMapping("/spots")
public class SpotController {
	
	private static Logger logger = Logger.getLogger(SpotController.class);
	
	
	@Autowired
	UserService userService;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	AccessTokenRepository accessTokenRepository;
	
	@Autowired
	UserLocationRepository userLocationRepository;	
	
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<Spot> getAllSpots(@RequestParam(required=false) Double latitude, @RequestParam(required=false) Double longitude, @RequestParam(required=false) Integer radius) throws Exception {
		List<Spot> spots = new ArrayList<Spot>();
		if(latitude == null && longitude==null && radius== null){
			spots = spotService.getAll();
		}else{
			if(latitude == null){
				throw new MissingServletRequestParameterException("latitude", "Double");
			}
			if(longitude == null){
				throw new MissingServletRequestParameterException("longitude", "Double");
			}
			if(radius == null){
				throw new MissingServletRequestParameterException("radius", "Integer");
			}
			spots = spotService.getByLatitudeLongitudeAndRadius(latitude, longitude, radius);
			
			
			
			UserLocation userLocation = new UserLocation();
			userLocation.setLocation(spotService.createPoint(longitude, latitude));
			userLocation.setUser(userService.getCurrentUser());
			
			userLocationRepository.save(userLocation);			
		}
		
		
		
		
		return spots;
	}
	
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public Spot getSpot(@PathVariable Long id) {
		Spot spot = spotService.getSpot(id);		
		return spot;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
	@ResponseBody
    public Spot addSpot(@RequestBody String json) throws Exception {
		Spot spot = spotService.json2Spot(json);	
		spot = spotService.saveSpot(spot);
		return spot;
	}	
}
