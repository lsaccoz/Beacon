package com.bcn.beacon.beacon.Data.Models;


import java.util.Date;

/**
 * Created by neema on 2016-10-16.
 * Edited by epekel on 2016-10-23.
 */
public class Event {

    private Long _id;
    private String uuid;
    private String name;
    private String hostId;
    private int num_attendees;
    private String locationId;
    private String[] attendee_Ids;
    private String timeStart_Id;
    private String timeEnd_Id;
    private String[] postIds;
    private String[] tags;
    private double[] location;
    private String description;

    // temporary distance variable addition
    private double distance;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String name, String hostId, double latitude, double longitude, String timeStart_Id, String description) {
        this.setName(name);
        this.setHostId(hostId);
        this.setLocation(latitude, longitude);
        this.setTimeStart_Id(timeStart_Id);
        this.setDescription(description);
    }

    public String getId(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public String getHostId(){
        return hostId;
    }

    public int getNumAttendees(){
        return num_attendees;
    }

    public String getLocationId(){
        return locationId;
    }

    public String[] getAttendee_Ids(){
        return attendee_Ids;
    }

    public String getTimeStart_Id(){
        return timeStart_Id;
    }

    public String getTimeEnd_Id(){
        return timeEnd_Id;
    }

    public String[] getPostIds(){
        return postIds;
    }

    public String[] getTags(){
        return tags;
    }

    public double getDistance() { return distance; }

    public double[] getLocation() { return location; }

    public String getDescription() { return description; }

    public void setId(String id){
        this.uuid = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setHostId(String hostId){
        this.hostId = hostId;
    }

    public void setNumAttendees(int num_attendees){
        this.num_attendees = num_attendees;
    }

    public void setLocationId(String locationId){
        this.locationId = locationId;
    }

    public void setAttendee_Ids(String[] attendee_Ids){
        this.attendee_Ids = attendee_Ids;
    }

    public void setTimeStart_Id(String timeStart_Id){
        this.timeStart_Id = timeStart_Id;
    }

    public void setTimeEnd_Id(String timeEnd_Id){
        this.timeEnd_Id = timeEnd_Id;
    }

    public void setPosts(String[] postIds){
        this.postIds = postIds;
    }

    public void setTags(String[] tags){
        this.tags = tags;
    }

    public void setDistance(double distance) { this.distance = distance; }

    public void setLocation(double latitude, double longitude) {
        // convention = {ycoord, xcoord}
        double[] location = {latitude, longitude};
        this.location = location;

    }

    public void setDescription(String description) { this.description = description; }




}
