package com.synaway.oneplaces.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.dto.ReportParamsDTO;
import com.synaway.oneplaces.model.Spot;
import com.synaway.oneplaces.model.User;
import com.synaway.oneplaces.repository.UserRepository;
import com.synaway.oneplaces.service.ReportService;

@Component
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ActivityReportDTO activityReport(ReportParamsDTO params) {
        ActivityReportDTO result = new ActivityReportDTO();

        Iterable<User> users;
        if (params.getUsers() == null) {
            users = userRepository.findAll();
        } else if (params.getUsers().size() > 0) {
            users = userRepository.findAll(params.getUsers());
        } else {
            users = new ArrayList<User>();
        }

        Long activeUsers = entityManager
                .createQuery(
                        "SELECT COUNT(DISTINCT ul.user) FROM UserLocation ul WHERE ul.timestamp BETWEEN :from AND :to AND ul.user IN(:users)",
                        Long.class).setParameter("from", params.getFrom()).setParameter("to", params.getTo())
                .setParameter("users", users).getSingleResult();

        result.setActiveUsers(activeUsers);

        // Number of clicks.
        Long greenClickCount = 0L;
        Long redClickCount = 0L;

        if (params.getStatus() != null && params.getStatus().equals("free")) {
            greenClickCount = getClickCount(params, null);
        } else if (params.getStatus() != null && params.getStatus().equals("occupied")) {
            redClickCount = getClickCount(params, null);
        } else {
            params.setStatus("free");
            greenClickCount = getClickCount(params, null);
            params.setStatus("occupied");
            redClickCount = getClickCount(params, null);
        }

        // Red spots required 2 clicks (first marked as green, then as red).
        result.setGreenRedClickCount(greenClickCount + 2 * redClickCount);

        // Calculate the average.

        if (activeUsers > 0) {
            result.setAverageClicksPerUser(result.getGreenRedClickCount() / Double.valueOf(activeUsers));
        } else {
            result.setAverageClicksPerUser(Double.valueOf(0));
        }

        return result;
    }

    @Override
    public ActivityReportDTO activityReport(ReportParamsDTO params, int zoom, int x, int y) {
        ActivityReportDTO result = new ActivityReportDTO();
        BoundingBox box = TileHelper.tile2boundingBox(x, y, zoom);
        // Number of clicks.
        Long greenClickCount = 0L;
        Long redClickCount = 0L;

        if (params.getStatus() != null && params.getStatus().equals("free")) {
            greenClickCount = getClickCount(params, box);
        } else if (params.getStatus() != null && params.getStatus().equals("occupied")) {
            redClickCount = getClickCount(params, box);
        } else {
            params.setStatus("free");
            greenClickCount = getClickCount(params, box);
            params.setStatus("occupied");
            redClickCount = getClickCount(params, box);
        }

        // Red spots required 2 clicks (first marked as green, then as red).
        result.setGreenRedClickCount(greenClickCount + 2 * redClickCount);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Spot> getSpots(ReportParamsDTO params, int zoom, int x, int y) {
        BoundingBox boundingBox = TileHelper.tile2boundingBox(x, y, zoom);
        Iterable<User> users;
        if (params.getUsers() == null) {
            users = userRepository.findAll();
        } else if (params.getUsers().size() > 0) {
            users = userRepository.findAll(params.getUsers());
        } else {
            return new ArrayList<Spot>();
        }
        String usersIdList = "";
        for (User user : users) {
            usersIdList += (usersIdList.length() > 0) ? ", " : "";
            usersIdList += user.getId();
        }
        return entityManager
                .createNativeQuery(
                        "SELECT * FROM Spot s WHERE  s.flag IS NULL AND s.created_at BETWEEN ?1 AND ?2 AND (?3 = 'both' OR status = ?3)"
                                + " AND ST_Within(location, ST_MakeEnvelope(?4, ?5, ?6, ?7, 4326))"
                                + " AND user_id IN(" + usersIdList + ")", Spot.class).setParameter(1, params.getFrom())
                .setParameter(2, params.getTo()).setParameter(3, params.getStatus())
                .setParameter(5, boundingBox.getNorth()).setParameter(4, boundingBox.getWest())
                .setParameter(7, boundingBox.getSouth()).setParameter(6, boundingBox.getEast()).getResultList();

    }

    protected Long getClickCount(ReportParamsDTO params, BoundingBox boundingBox) {
        Iterable<User> users;
        if (params.getUsers() == null) {
            users = userRepository.findAll();
        } else if (params.getUsers().size() > 0) {
            users = userRepository.findAll(params.getUsers());
        } else {
            return 0L;
        }

        if (boundingBox == null) {
            return entityManager
                    .createQuery(
                            "SELECT COUNT(*) FROM Spot s WHERE s.flag IS NULL AND s.timestamp BETWEEN :from AND :to AND status = :status AND user IN(:users)",
                            Long.class).setParameter("from", params.getFrom()).setParameter("to", params.getTo())
                    .setParameter("status", params.getStatus()).setParameter("users", users).getSingleResult();
        } else {
            String usersIdList = "";
            for (User user : users) {
                usersIdList += (usersIdList.length() > 0) ? ", " : "";
                usersIdList += user.getId();
            }
            return ((BigInteger) entityManager
                    .createNativeQuery(
                            "SELECT COUNT(*) FROM Spot s WHERE  s.flag IS NULL AND s.created_at BETWEEN ?1 AND ?2 AND status = ?3"
                                    + " AND ST_Within(location, ST_MakeEnvelope(?4, ?5, ?6, ?7, 4326))"
                                    + " AND user_id IN(" + usersIdList + ")").setParameter(1, params.getFrom())
                    .setParameter(2, params.getTo()).setParameter(3, params.getStatus())
                    .setParameter(5, boundingBox.getNorth()).setParameter(4, boundingBox.getWest())
                    .setParameter(7, boundingBox.getSouth()).setParameter(6, boundingBox.getEast()).getSingleResult())
                    .longValue();
        }
    }
}
