package com.moutamid.talk_togather.Models;

public class RoomDetails {

    private String id;
    private String title;
    private String description;
    private long timestamp;
    private String date;
    private String category;
    private boolean live;
    private String creatorId;

    public RoomDetails(){

    }

    public RoomDetails(String id,String creatorId, String title, String description,
                       long timestamp,String date, String category) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.date = date;
        this.category = category;
    }

    public RoomDetails(String id,String creatorId, String title, String description, long timestamp,
                       String category,boolean live) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.category = category;
        this.live = live;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}
