package com.synaway.oneplaces.service;

import java.util.Date;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.dto.ReportParamsDTO;

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
    ActivityReportDTO activityReport(ReportParamsDTO params);

    /**
     * Generates activity report for a tile (rectangular area).
     * 
     * @param fromDate
     *            beginning of the report time period
     * @param toDate
     *            end of the report time period
     * @param zoom
     *            zoom level
     * @param x
     *            tile x
     * @param y
     *            tile y
     * @return data object containing the report
     */
    ActivityReportDTO activityReport(Date fromDate, Date toDate, int zoom, int x, int y);


}
