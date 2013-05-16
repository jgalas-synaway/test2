package com.synaway.oneplaces.controller.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import com.synaway.oneplaces.services.SpotService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;


@Transactional
@Controller
@RequestMapping("/api")
public class SpotController {
	
	private static Logger logger = Logger.getLogger(SpotController.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SpotService spotService;
	
	
	@RequestMapping(value="/spots", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public List<Spot> getAllSpots(@RequestParam double latitude) {
		List<Spot> spots = spotService.getAll();
		return spots;
	}
	
	
	
	@RequestMapping(value="/spots/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public Spot getSpot(@PathVariable Long id) {
		Spot spot = spotService.getSpot(id);		
		return spot;
	}
	
	
	@RequestMapping(value="/spots", method = RequestMethod.POST, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public Spot addSpot(@RequestBody String json) throws Exception {
		Spot spot = spotService.json2Spot(json);	
		spot = spotService.saveSpot(spot);
		return spot;
	}	
	
	
	@RequestMapping(value="/test", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
	@ResponseBody
    public String test() {
		Spot spot = new Spot();
		
		spot.setLocation(spotService.createPoint(0, 0));
		

		
		User user = new User();
		user.setFirstName("test");
		user.setLastName("test");
		user = userRepository.save(user);
		spot.setUser(user);
		//spotRepository.save(spot);
		
		
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value="/cartodb")
    public @ResponseBody String initDevicePairing() {
		String res = "";
		for(int i=0; i < 10; i++) {
            try {
                long startRequest = new Date().getTime();
                final HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
                HttpConnectionParams.setSoTimeout(httpParams, 35000);
                HttpClient client = new DefaultHttpClient(httpParams);
                HttpGet request = new HttpGet(createURL());
                HttpResponse response = client.execute(request);

                BufferedReader br2 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        Charset.forName("UTF-8")));
                long requestDuration = new Date().getTime() - startRequest;

                res += br2.readLine()+"<br />";
            } catch (Exception e) {
            	logger.error(e.getMessage(),e);
            }
        }
    	return res;
        
    }
	
	private String createURL() throws UnsupportedEncodingException{
		double min = 50.0521;
		double max = 50.0787;
		Random r = new Random();
		double latitude = min+(r.nextDouble()*(max-min));

		min = 19.883;
		max = 19.969;
		double longitude = min+(r.nextDouble()*(max-min));
		
		boolean active = r.nextBoolean();
		
		String sql = URLEncoder.encode("INSERT INTO spots (the_geom, active) VALUES (ST_SetSRID(ST_Point("+longitude+", "+latitude+"),4326), "+active+")","UTF-8");
		String URI = "q="+sql+"&api_key=e8097e3a9c84902ddeadbe8fa0e6683afbfa9352";
		String URL = "http://1places.cartodb.com/api/v2/sql?"+URI;
		logger.info(URL);
		return URL;
	}
}
