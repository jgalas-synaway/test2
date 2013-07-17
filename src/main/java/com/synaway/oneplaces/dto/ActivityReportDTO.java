package com.synaway.oneplaces.dto;

import java.io.Serializable;

/**
 * Includes activity report data.
 * 
 */
public class ActivityReportDTO implements Serializable {

    private static final long serialVersionUID = -6592393933124505519L;

    /**
     * Number of active users.
     */
    private Long activeUsers;

    /**
     * Number of green and red clicks.
     */
    private Long greenRedClickCount;

    /**
     * Number of average clicks per user;
     */
    private Double averageClicksPerUser;

    public ActivityReportDTO() {
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getGreenRedClickCount() {
        return greenRedClickCount;
    }

    public void setGreenRedClickCount(Long greenRedClickCount) {
        this.greenRedClickCount = greenRedClickCount;
    }

    public Double getAverageClicksPerUser() {
        return averageClicksPerUser;
    }

    public void setAverageClicksPerUser(Double averageClicksPerUser) {
        this.averageClicksPerUser = averageClicksPerUser;
    }
}
