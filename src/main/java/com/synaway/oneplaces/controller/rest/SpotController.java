package com.synaway.oneplaces.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synaway.oneplaces.exception.UserException;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.UserLocation;
import com.synaway.oneplaces.repository.SpotRepository;
import com.synaway.oneplaces.repository.UserLocationRepository;
import com.synaway.oneplaces.service.SpotService;
import com.synaway.oneplaces.service.UserService;

@Transactional
@Controller
@RequestMapping("/spots")
public class SpotController {

    private static final Logger logger = Logger.getLogger(SpotController.class);

    @Autowired
    @Value("${city.latitude}")
    private String cityLatitude = "0";

    @Autowired
    @Value("${city.longitude}")
    private String cityLongitude = "0";

    @Autowired
    @Value("${city.radius}")
    private String cityRadius = "0";

    @Autowired
    private UserService userService;

    @Autowired
    private SpotService spotService;

    @Autowired
    private SpotRepository spotRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
    @ResponseBody
    public Map<String, Object> getAllSpots(@RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude, @RequestParam(required = false) Integer radius,
            @RequestParam(required = false) Boolean tracking) throws MissingServletRequestParameterException {
        List<Spot> spots = new ArrayList<Spot>();
        Map<String, Object> response = new HashMap<String, Object>();

        if (latitude == null) {
            throw new MissingServletRequestParameterException("latitude", "Double");
        }
        if (longitude == null) {
            throw new MissingServletRequestParameterException("longitude", "Double");
        }
        if (radius == null) {
            throw new MissingServletRequestParameterException("radius", "Integer");
        }
        spots = spotService.getByLatitudeLongitudeAndRadius(latitude, longitude, radius);

        if (tracking == null || tracking.booleanValue()) {

            UserLocation userLocation = new UserLocation();
            userLocation.setLocation(spotService.createPoint(longitude, latitude));
            userLocation.setUser(userService.getCurrentUser());

            userLocationRepository.save(userLocation);
        }

        Long ttl9 = spotService.count(Double.valueOf(cityLatitude), Double.valueOf(cityLongitude),
                Integer.valueOf(cityRadius), 540, 360);
        Long ttl6 = spotService.count(Double.valueOf(cityLatitude), Double.valueOf(cityLongitude),
                Integer.valueOf(cityRadius), 360, 180);
        Long ttl3 = spotService.count(Double.valueOf(cityLatitude), Double.valueOf(cityLongitude),
                Integer.valueOf(cityRadius), 180, 0);

        response.put("ttl3", ttl9); // XXX change 3 and 9 back
        response.put("ttl6", ttl6);
        response.put("ttl9", ttl3); // XXX change 3 and 9 back

        logger.debug("Returning " + spots.size() + " spot(s) [lat = " + latitude + ", lon = " + longitude
                + ", radius = " + radius + "], 3/6/9 = " + ttl3 + "/" + ttl6 + "/" + ttl9);

        response.put("spots", spots);

        return response;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json", produces = "application/json")
    @ResponseBody
    public Spot getSpot(@PathVariable Long id) {
        return spotService.getSpot(id);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json")
    @ResponseBody
    public Spot addSpot(@RequestBody String json) throws MissingServletRequestParameterException, UserException,
            IOException {
        return spotService.saveSpot(spotService.json2Spot(json));
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Spot updateSpot(@RequestBody String json) throws IOException, MissingServletRequestParameterException,
            UserException {
        Spot spot = spotService.json2Spot(json);
        if (spot.getId() == null) {
            throw new MissingServletRequestParameterException("spotId", "Long");
        }
        return spotService.updateSpot(spot);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Spot deleteSpot(@PathVariable Long id) {
        Spot spot = spotService.getSpot(id);
        spotRepository.delete(id);
        return spot;
    }

    @RequestMapping(value = "/{id}/occupy", method = RequestMethod.PUT)
    @ResponseBody
    public Spot setOccupy(@PathVariable Long id) {
        Spot spot = spotService.getSpot(id);
        spot.setStatus("occupied");
        spot = spotService.saveSpot(spot);

        logger.debug("Occupied spot: " + spot);

        return spot;
    }

    @RequestMapping("/random")
    @ResponseBody
    public Spot addSpot(@RequestParam Double minLatitude, @RequestParam Double minLongitude,
            @RequestParam Double maxLatitude, @RequestParam Double maxLongitude) {
        Spot spot = new Spot();
        spot.setTimestamp(new Date());
        spot.setUser(userService.getAll().get(0));
        spot.setStatus("free");
        spot.setFlag("fake");

        Random r = new Random();
        double latitude = minLatitude + (maxLatitude - minLatitude) * r.nextDouble();
        double longitude = minLongitude + (maxLongitude - minLongitude) * r.nextDouble();

        spot.setLocation(spotService.createPoint(latitude, longitude));

        spot = spotService.saveSpot(spot);

        logger.debug("Saved spot: " + spot);

        return spot;
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> dataTablesSpot(@RequestParam(required = false) Long iDisplayStart,
            @RequestParam(required = false) Long iDisplayLength, @RequestParam(required = false) int sEcho,
            @RequestParam(value = "mDataProp_0", required = false) String mDataProp0,
            @RequestParam(value = "mDataProp_1", required = false) String mDataProp1,
            @RequestParam(value = "mDataProp_2", required = false) String mDataProp2,
            @RequestParam(value = "mDataProp_3", required = false) String mDataProp3,
            @RequestParam(value = "mDataProp_4", required = false) String mDataProp4,
            @RequestParam(value = "iSortCol_0", required = false) int iSortCol,
            @RequestParam(value = "sSortDir_0", required = false) String sSortDir,
            @RequestParam(required = false) String sSearch) {

        List<String> cols = new ArrayList<String>();
        cols.add(0, mDataProp0);
        cols.add(1, mDataProp1);
        cols.add(2, mDataProp2);
        cols.add(3, mDataProp3);
        cols.add(4, mDataProp4);

        Map<String, Object> response = new HashMap<String, Object>();

        response.put("aaData", spotService.getAll(cols.get(iSortCol), sSortDir, iDisplayStart, iDisplayLength));
        response.put("iTotalRecords", spotRepository.count());

        response.put("iTotalDisplayRecords", spotRepository.count());
        response.put("sEcho", sEcho);

        return response;

    }
}
