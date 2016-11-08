package com.bcn.beacon.beacon.Data.Models;


/**
 * Created by neema on 2016-10-16.
 */
public class Comment {

    private Long _id;

    private String uuid;
    private String userId;
    private String text;
    private String dateId;

    public String getId(){
        return uuid;
    }

    public String getText(){
        return text;
    }

    public String getDateId(){
        return dateId;
    }

    public void setId(String uuid){
        this.uuid = uuid;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setText(String text){
        this.text = text;
    }

}