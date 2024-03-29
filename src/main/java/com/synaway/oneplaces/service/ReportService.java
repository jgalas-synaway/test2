package com.synaway.oneplaces.service;

import java.util.List;

import com.synaway.oneplaces.dto.ActivityReportDTO;
import com.synaway.oneplaces.dto.ReportParamsDTO;
import com.synaway.oneplaces.model.Spot;

/**
 * API for reports.
 * 
 */
public interface ReportService {
    /**
     * Generates activity report.
     * 
     * @param params
     *            contains filter parameters used for reporting
     * @return data object containing the report
     */
    ActivityReportDTO activityReport(ReportParamsDTO params);

    /**
     * Generates activity report for a tile (rectangular area).
     * 
     * @param params
     *            contains filter parameters used for reporting
     * @param zoom
     *            zoom level
     * @param x
     *            tile x
     * @param y
     *            tile y
     * @return data object containing the report
     */
    ActivityReportDTO activityReport(ReportParamsDTO params, int zoom, int x, int y);

    /**
     * Returns spots for a tile (rectangular area)
     * 
     * @param params
     *            contains filter parameters used for reporting
     * @param zoom
     *            zoom level
     * @param x
     *            tile x
     * @param y
     *            tile y
     * @return list of spots for a tile
     */
    List<Spot> getSpots(ReportParamsDTO params, int zoom, int x, int y);

}
