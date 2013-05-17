package com.synaway.oneplaces.controller.rest;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.model.AccessToken;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;

@Transactional
@Controller
@RequestMapping("/users")
public class UserController {

	private static Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<User> getAllUsers() {
		List<User> spots = userService.getAll();
		return spots;
	}
	
	
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User getUser(@PathVariable Long id) {
		User spot = userService.getUser(id);		
		return spot;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json", headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public User addUser(@RequestBody User user) throws Exception{	
		if(user.getId() != null){
			throw new Exception("field id not allowed");
		}
		if(user.getLogin() == null){
			throw new Exception("Missing field login");
		}
		if(user.getPassword() == null){
			throw new Exception("Missing field password");
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
