package com.moutamid.talk_togather.Models;

public class Model_Catagories {
    String  time ;
    int image1,heading;

    public Model_Catagories() {
    }

    public Model_Catagories(int heading, String time, int image1) {
        this.heading = heading;
        this.time = time;
        this.image1 = image1;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getImage1() {
        return image1;
    }

    public void setImage1(int image1) {
        this.image1 = image1;
    }
}
