package com.synaway.oneplaces.controller.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.SpotService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;


@Transactional
@Controller
@RequestMapping("/spots")
public class SpotController {
	
	private static Logger logger = Logger.getLogger(SpotController.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SpotService spotService;
	
	
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<Spot> getAllSpots(@RequestParam(required=false) Double latitude, @RequestParam(required=false) Double longitude, @RequestParam(required=false) Integer radius) throws Exception {
		List<Spot> spots = new ArrayList<Spot>();
		if(latitude == null && longitude==null && radius== null){
			spots = spotService.getAll();
		}else{
			if(latitude == null){
				throw new Exception("Missing argument latitude");
			}
			if(longitude == null){
				throw new Exception("Missing argument longitude");
			}
			if(radius == null){
				throw new Exception("Missing argument radius");
			}
			spots = spotService.getByLatitudeLongitudeAndRadius(latitude, longitude, radius);
		}
		return spots;
	}
	
	
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public Spot getSpot(@PathVariable Long id) {
		Spot spot = spotService.getSpot(id);		
		return spot;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public Spot addSpot(@RequestBody String json) throws Exception {
		Spot spot = spotService.json2Spot(json);	
		spot = spotService.saveSpot(spot);
		return spot;
	}	
}
