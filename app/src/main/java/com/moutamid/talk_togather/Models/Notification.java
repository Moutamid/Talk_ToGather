package com.moutamid.talk_togather.Models;

public class Notification {

    private long id;
    private String fromUserId;
    private String fromPostId;
    private String notificationType;
    private boolean seen;
    private long notificationTime;

    public Notification(){

    }

    public Notification(long id, String fromUserId, String fromPostId, String notificationType, boolean seen, long notificationTime) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.fromPostId = fromPostId;
        this.notificationType = notificationType;
        this.seen = seen;
        this.notificationTime = notificationTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFromPostId() {
        return fromPostId;
    }

    public void setFromPostId(String fromPostId) {
        this.fromPostId = fromPostId;
    }

    public String getFromUserId() { return fromUserId; }

    public String getNotificationType() { return notificationType; }


    public long getNotificationTime() { return notificationTime; }

    public void setFromUserId(String fromUserId) { this.fromUserId = fromUserId; }

    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setNotificationTime(long notificationTime) { this.notificationTime = notificationTime; }

}
