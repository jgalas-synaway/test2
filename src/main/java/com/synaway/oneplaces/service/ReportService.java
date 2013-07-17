package com.synaway.oneplaces.service;

import java.util.Date;

import com.synaway.oneplaces.dto.ActivityReportDTO;

/**
 * API for reports.
 * 
 */
public interface ReportService {
    /**
     * Generates activity report.
     * 
     * @param fromDate
     *            beginning of the report time period
     * @param toDate
     *            end of the report time period
     * @return data object containing the report
     */
    ActivityReportDTO activityReport(Date fromDate, Date toDate);
}
