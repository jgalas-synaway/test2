package com.synaway.oneplaces.controller.rest;

import java.util.Calendar;
import java.util.List;

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

import com.synaway.oneplaces.exception.GeneralException;
import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserLocationService;
import com.synaway.oneplaces.service.UserService;

@Transactional
@Controller
@RequestMapping("/users")
public class UserController {
	
	private static final int DEFAULT_LIMIT = 20;
	private static final int DEFAULT_OFFSET = 0;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SpotService spotService;
	
	@Autowired
	private UserLocationService userLocationService;
		
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<User> getAllUsers() {
		return userService.getAll();
	}
	
	
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User getUser(@PathVariable Long id) {
		return userService.getUser(id);		
	}
	
	@RequestMapping(method = RequestMethod.PUT, consumes="application/json", produces = "application/json")
	@ResponseBody
    public User updateUser(@RequestBody User user) throws MissingServletRequestParameterException {
		if(user.getId() == null){
			throw new MissingServletRequestParameterException("id","Long");
		}		
		return userService.updateUser(user);
	}
	
	@RequestMapping(value="/me", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User getCurrentUser() {
		return userService.getCurrentUser();		
	}
	
	@RequestMapping(value="/{id}/spots", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<Spot> getUserSpots(@PathVariable Long id, @RequestParam(required=false) Integer limit, @RequestParam(required=false) Integer offset) {
		User user = userService.getUser(id);
		Integer lmt = limit;
		Integer off = offset;
		if(lmt == null){
			lmt = DEFAULT_LIMIT;
		}
		
		if(off == null){
			off = DEFAULT_OFFSET;
		}
		
		return spotService.getByUser(user, lmt, off);
	}
	
	@RequestMapping(value="/{id}/locations", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<UserLocation> getUserLocations(@PathVariable Long id, @RequestParam(required=false) Integer limit, @RequestParam(required=false) Integer offset) {
		User user = userService.getUser(id);
		Integer lmt = limit;
		Integer off = offset;
		if(lmt == null){
			lmt = DEFAULT_LIMIT;
		}
		
		if(off == null){
			off = DEFAULT_OFFSET;
		}
		
		return  userLocationService.getByUser(user, lmt, off);		
	}
	
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json", headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User addUser(@RequestBody User user) throws GeneralException, MissingServletRequestParameterException{	
		if(user.getId() != null){
			throw new GeneralException("field id not allowed");
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
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
    public User deleteSpot(@PathVariable Long id)  {
		
		return userService.delete(id);
	}
	
	@RequestMapping(value="/auth", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public AccessToken getToken(@RequestParam String login, @RequestParam String password) throws UserException {
		return userService.getToken(login, password);
	}
	
	
}
