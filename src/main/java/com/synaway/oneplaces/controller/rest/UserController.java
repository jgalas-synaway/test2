package com.synaway.oneplaces.controller.rest;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserLocationService;
import com.synaway.oneplaces.service.UserService;

@Transactional
@Controller
@RequestMapping("/users")
public class UserController {

	private static Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	SpotService spotService;
	
	@Autowired
	UserLocationService userLocationService;
	
	
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<User> getAllUsers() {
		List<User> spots = userService.getAll();
		return spots;
	}
	
	
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User getUser(@PathVariable Long id) {
		User user = userService.getUser(id);		
		return user;
	}
	
	@RequestMapping(value="/me", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User getCurrentUser() {
		User user = userService.getCurrentUser();		
		return user;
	}
	
	@RequestMapping(value="/{id}/spots", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<Spot> getUserSpots(@PathVariable Long id, @RequestParam(required=false) Integer limit, @RequestParam(required=false) Integer offset) {
		User user = userService.getUser(id);
		
		if(limit == null){
			limit = 20;
		}
		
		if(offset == null){
			offset = 0;
		}
		
		List<Spot> spots = spotService.getByUser(user, limit, offset);
		return spots;
	}
	
	@RequestMapping(value="/{id}/locations", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<UserLocation> getUserLocations(@PathVariable Long id, @RequestParam(required=false) Integer limit, @RequestParam(required=false) Integer offset) {
		User user = userService.getUser(id);
		
		if(limit == null){
			limit = 20;
		}
		
		if(offset == null){
			offset = 0;
		}
		
		List<UserLocation> userLocations = userLocationService.getByUser(user, limit, offset);
		return userLocations;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json", headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User addUser(@RequestBody User user) throws Exception{	
		if(user.getId() != null){
			throw new Exception("field id not allowed");
		}
		if(user.getLogin() == null){
			throw new MissingServletRequestParameterException("login","String");
		}
		if(user.getPassword() == null){
			throw new MissingServletRequestParameterException("password","String");
		}
		user.setCreationDate(Calendar.getInstance().getTime());
		user.setModificationDate(Calendar.getInstance().getTime());
		return userService.saveUser(user);
	}	
	
	@RequestMapping(value="/auth", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public AccessToken getToken(@RequestParam String login, @RequestParam String password) throws Exception {
		
		return userService.getToken(login, password);
	}
	
	
}
