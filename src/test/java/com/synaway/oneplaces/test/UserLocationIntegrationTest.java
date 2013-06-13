package com.synaway.oneplaces.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonassert.JsonAssert;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.AccessTokenRepository;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.UserService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

public class UserLocationIntegrationTest{

	private static Logger logger = Logger.getLogger(UserLocationIntegrationTest.class);

	@Test
	public void serializeShouldReturnProperData() throws JsonGenerationException, JsonMappingException, IOException{
		UserLocation userLocation = new UserLocation();
		userLocation.setTimestamp(null);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(userLocation);
		
		JsonAssert.with(json)
			.assertThat("$.id", Matchers.nullValue())
			.assertThat("$.longitude", Matchers.nullValue())
			.assertThat("$.timestamp", Matchers.nullValue())
			.assertThat("$.latitude", Matchers.nullValue())
			.assertThat("$.user", Matchers.nullValue());
		
		userLocation = new UserLocation();
		userLocation.setId(1l);
		userLocation.setUser(new User());
		userLocation.setLocation(createPoint(50.50, 19.07));
		
		mapper = new ObjectMapper();
		
		
		json = mapper.writeValueAsString(userLocation);

		JsonAssert.with(json)
			.assertThat("$.id", Matchers.equalTo(1))
			.assertThat("$.longitude", Matchers.equalTo(50.50))
			.assertThat("$.latitude", Matchers.equalTo(19.07))
			.assertThat("$.timestamp", Matchers.notNullValue())
			.assertThat("$.user", Matchers.notNullValue());
	}
	
	public Point createPoint(double longitude, double latitude){
		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);		
        Coordinate coord = new Coordinate( longitude, latitude );
		return gf.createPoint( coord );
	}
}
