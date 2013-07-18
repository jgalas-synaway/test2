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

        Long activeUsers = entityManager
                .createQuery(
                        "SELECT COUNT(DISTINCT ul.user) FROM UserLocation ul WHERE ul.timestamp BETWEEN :from AND :to",
                        Long.class).setParameter("from", fromDate).setParameter("to", toDate).getSingleResult();

        result.setActiveUsers(activeUsers);

        return result;
    }

}