package com.bcn.beacon.beacon.Data.Models;


/**
 * Created by neema on 2016-10-16.
 */

public class Event {

    private Long _id;
    private String uuid;
    private String name;
    private String hostId;
    private int num_attendees;
    private String locationId;
    private String timeStart_Id;
    private String timeEnd_Id;


    public String getId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getHostId() {
        return hostId;
    }

    public int getNumAttendees() {
        return num_attendees;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getTimeStart_Id() {
        return timeStart_Id;
    }

    public String getTimeEnd_Id() {
        return timeEnd_Id;
    }

    public void setId(String id) {
        this.uuid = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setNumAttendees(int num_attendees) {
        this.num_attendees = num_attendees;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setTimeStart_Id(String timeStart_Id) {
        this.timeStart_Id = timeStart_Id;
    }

    public void setTimeEnd_Id(String timeEnd_Id) {
        this.timeEnd_Id = timeEnd_Id;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getNum_attendees() {
        return this.num_attendees;
    }

    public void setNum_attendees(int num_attendees) {
        this.num_attendees = num_attendees;
    }


}
