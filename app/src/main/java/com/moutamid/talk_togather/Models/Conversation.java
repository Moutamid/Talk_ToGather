package com.moutamid.talk_togather.Models;

public class Conversation {

    private String userUid;
    private String chatWithId;
    private String chatId;
    private String lastMessage;
    private User user;
    private String type;
    private long timestamp;
    private int unreadChatCount = 0;

    public Conversation() {
    }

    public Conversation(String type,String userUid, String chatWithId, String lastMessage,
                        long timestamp, int unreadChatCount) {
        this.type = type;
        this.userUid = userUid;
        this.chatWithId = chatWithId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadChatCount = unreadChatCount;
    }

    public Conversation(String type,String userUid, String chatWithId, String lastMessage,
                        long timestamp) {
        this.type = type;
        this.userUid = userUid;
        this.chatWithId = chatWithId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getChatWithId() {
        return chatWithId;
    }

    public void setChatWithId(String chatWith) {
        this.chatWithId = chatWith;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getUnreadChatCount() {
        return unreadChatCount;
    }

    public void setUnreadChatCount(int unreadChatCount) {
        this.unreadChatCount = unreadChatCount;
    }
}
