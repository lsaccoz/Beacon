package com.bcn.beacon.beacon.Data.Models;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by neema on 2016-10-16.
 */

@Entity(

        active = true
)
public class Event {

    @Id
    private Long _id;

    @NotNull
    private String uuid;
    private String name;
    private String hostId;
    private int num_attendees;
    private String locationId;
    //private String[] attendee_Ids;
    private String timeStart_Id;
    private String timeEnd_Id;
    //private String[] postIds;
    //private String[] tags;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1542254534)
    private transient EventDao myDao;


    @Generated(hash = 710089698)
    public Event(Long _id, @NotNull String uuid, String name, String hostId,
            int num_attendees, String locationId, String timeStart_Id,
            String timeEnd_Id) {
        this._id = _id;
        this.uuid = uuid;
        this.name = name;
        this.hostId = hostId;
        this.num_attendees = num_attendees;
        this.locationId = locationId;
        this.timeStart_Id = timeStart_Id;
        this.timeEnd_Id = timeEnd_Id;
    }

    @Generated(hash = 344677835)
    public Event() {
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

//    public String[] getAttendee_Ids(){
//        return attendee_Ids;
//    }

    public String getTimeStart_Id(){
        return timeStart_Id;
    }

    public String getTimeEnd_Id(){
        return timeEnd_Id;
    }

//    public String[] getPostIds(){
//        return postIds;
//    }

//    public String[] getTags(){
//        return tags;
//    }

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

//    public void setAttendee_Ids(String[] attendee_Ids){
//        this.attendee_Ids = attendee_Ids;
//    }

    public void setTimeStart_Id(String timeStart_Id){
        this.timeStart_Id = timeStart_Id;
    }

    public void setTimeEnd_Id(String timeEnd_Id){
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

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1459865304)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEventDao() : null;
    }

//    public void setPosts(String[] postIds){
//        this.postIds = postIds;
//    }

//    public void setTags(String[] tags){
//        this.tags = tags;
//    }




}
