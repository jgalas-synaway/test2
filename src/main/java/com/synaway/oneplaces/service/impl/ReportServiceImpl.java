package com.synaway.oneplaces.service.impl;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.service.ReportService;

@Component
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ActivityReportDTO activityReport(Date fromDate, Date toDate) {
        ActivityReportDTO result = new ActivityReportDTO();

        // Active users count.

        Long activeUsers = entityManager
                .createQuery(
                        "SELECT COUNT(DISTINCT ul.user) FROM UserLocation ul WHERE ul.timestamp BETWEEN :from AND :to",
                        Long.class).setParameter("from", fromDate).setParameter("to", toDate).getSingleResult();

        result.setActiveUsers(activeUsers);

        // Number of clicks.

        Long greenClickCount = entityManager
                .createQuery("SELECT COUNT(*) FROM Spot s WHERE s.timestamp BETWEEN :from AND :to AND status = 'free'",
                        Long.class).setParameter("from", fromDate).setParameter("to", toDate).getSingleResult();

        Long redClickCount = entityManager
                .createQuery(
                        "SELECT COUNT(*) FROM Spot s WHERE s.timestamp BETWEEN :from AND :to AND status = 'occupied'",
                        Long.class).setParameter("from", fromDate).setParameter("to", toDate).getSingleResult();

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

}
