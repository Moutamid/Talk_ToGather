package com.moutamid.talk_togather.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String id;
    private String phone;
    private String fname;
    private String lname;
    private String bio;
    private String email;
    private String password;
    private String imageUrl;
    private boolean remember;
    private String status;
    private long timestamp;
    private String facebookId;
    private String linkdinId;
    private String tiktokId;
    private String instaId;
    private String twitterId;

    public User(){

    }

    public User(String id, String phone, String fname, String lname, String bio, String email, String password, String imageUrl, boolean remember, String status, long timestamp) {
        this.id = id;
        this.phone = phone;
        this.fname = fname;
        this.lname = lname;
        this.bio = bio;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.remember = remember;
        this.status = status;
        this.timestamp = timestamp;
    }


    protected User(Parcel in) {
        id = in.readString();
        phone = in.readString();
        fname = in.readString();
        lname = in.readString();
        bio = in.readString();
        email = in.readString();
        password = in.readString();
        imageUrl = in.readString();
        remember = in.readByte() != 0;
        status = in.readString();
        timestamp = in.readLong();
        facebookId = in.readString();
        linkdinId = in.readString();
        tiktokId = in.readString();
        instaId = in.readString();
        twitterId = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFacebookId() {
        return facebookId;
    }

    public String getLinkdinId() {
        return linkdinId;
    }

    public String getTiktokId() {
        return tiktokId;
    }

    public String getInstaId() {
        return instaId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setLinkdinId(String linkdinId) {
        this.linkdinId = linkdinId;
    }

    public void setTiktokId(String tiktokId) {
        this.tiktokId = tiktokId;
    }

    public void setInstaId(String instaId) {
        this.instaId = instaId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }



    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(phone);
        parcel.writeString(fname);
        parcel.writeString(lname);
        parcel.writeString(bio);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(imageUrl);
        parcel.writeByte((byte) (remember ? 1 : 0));
        parcel.writeString(status);
        parcel.writeLong(timestamp);
    }
}
