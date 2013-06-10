package com.synaway.oneplaces.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.service.UserLocationService;

@Controller
@RequestMapping("/locations")
public class UserLocationController {
	
	@Autowired
	private UserLocationService userLocationService;

	@RequestMapping(value="/active", method=RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<UserLocation> getActiveLocations(){
		return userLocationService.getActive();
	}
}
