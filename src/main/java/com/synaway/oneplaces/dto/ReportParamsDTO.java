package com.synaway.oneplaces.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Includes reports parameters data
 * 
 * @author Łukasz Kracoń
 * 
 */
public class ReportParamsDTO implements Serializable {

    private static final long serialVersionUID = 5153705669628708115L;

    /**
     * Beginning of the reporting period
     */
    private Date from;

    /**
     * End of the reporting period
     */
    private Date to;

    /**
     * List of users id in included in report
     */
    private List<Long> users;

    /**
     * Status of posts to report ("free"/"occupied")
     */
    private String status;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        result = prime * result + ((users == null) ? 0 : users.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReportParamsDTO other = (ReportParamsDTO) obj;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        if (users == null) {
            if (other.users != null)
                return false;
        } else if (!users.equals(other.users))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ReportParamsDTO [from=" + from + ", to=" + to + ", users=" + users + ", activityKind=" + status + "]";
    }

}
