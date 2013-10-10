package com.synaway.oneplaces.service.impl;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.dto.ReportParamsDTO;
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

        // Active users count.
        

        Long activeUsers = entityManager
                .createQuery(
                        "SELECT COUNT(DISTINCT ul.user) FROM UserLocation ul WHERE ul.timestamp BETWEEN :from AND :to AND ul.user IN(:users)",
                        Long.class).setParameter("from", params.getFrom()).setParameter("to", params.getTo())
                .setParameter("users", userRepository.findAll(params.getUsers())).getSingleResult();

        result.setActiveUsers(activeUsers);

        // Number of clicks.

        Long greenClickCount = getClickCount(params, null, "free");
        Long redClickCount = getClickCount(params, null, "occupied");

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
    public ActivityReportDTO activityReport(Date fromDate, Date toDate, int zoom, int x, int y) {
        ActivityReportDTO result = new ActivityReportDTO();
        BoundingBox box = TileHelper.tile2boundingBox(x, y, zoom);
        // Number of clicks.

        // Long greenClickCount = getClickCount(fromDate, toDate, box, "free");
        // Long redClickCount = getClickCount(fromDate, toDate, box,
        // "occupied");
        //
        // // Red spots required 2 clicks (first marked as green, then as red).
        // result.setGreenRedClickCount(greenClickCount + 2 * redClickCount);
        return result;
    }

    protected Long getClickCount(ReportParamsDTO params, BoundingBox boundingBox, String status) {
        if (boundingBox == null) {
            return entityManager
                    .createQuery(
                            "SELECT COUNT(*) FROM Spot s WHERE s.timestamp BETWEEN :from AND :to AND status = :status AND user IN(:users)",
                            Long.class).setParameter("from", params.getFrom()).setParameter("to", params.getTo())
                    .setParameter("status", status).setParameter("users", userRepository.findAll(params.getUsers())).getSingleResult();
        } else {
            return ((BigInteger) entityManager
                    .createNativeQuery(
                            "SELECT COUNT(*) FROM Spot s WHERE s.created_at BETWEEN ?1 AND ?2 AND status = ?3"
                                    + " AND ST_Within(location, ST_MakeEnvelope(?4, ?5, ?6, ?7, 4326))")
                    .setParameter(1, params.getFrom()).setParameter(2, params.getTo()).setParameter(3, status)
                    .setParameter(5, boundingBox.getNorth()).setParameter(4, boundingBox.getWest())
                    .setParameter(7, boundingBox.getSouth()).setParameter(6, boundingBox.getEast()).getSingleResult())
                    .longValue();
        }
    }
}
