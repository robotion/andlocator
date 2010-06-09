package com.jaeckel.locator;

import android.location.Location;

import java.util.Date;

/**
 * User: biafra
 * Date: Jun 5, 2010
 * Time: 11:27:51 PM
 */
public class Position {

    Location location;
    String status;
    Date timestamp;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
