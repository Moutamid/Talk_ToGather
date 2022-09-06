package com.moutamid.talk_togather.Models;

public class BlockedUser {

    private String id;


    public BlockedUser(){

    }

    public BlockedUser(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
