package com.moutamid.talk_togather.Models;

public class Model_Detail {
    String heading , time , description ;
    int image1 , image2 , image3 ;

    public Model_Detail() {
    }

    public Model_Detail(String heading, String time, String description, int image1, int image2, int image3) {
        this.heading = heading;
        this.time = time;
        this.description = description;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage1() {
        return image1;
    }

    public void setImage1(int image1) {
        this.image1 = image1;
    }

    public int getImage2() {
        return image2;
    }

    public void setImage2(int image2) {
        this.image2 = image2;
    }

    public int getImage3() {
        return image3;
    }

    public void setImage3(int image3) {
        this.image3 = image3;
    }
}
