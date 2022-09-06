package com.moutamid.talk_togather.Models;

import java.util.List;

public class Chat {

    private String message;
    private String senderUid;
    private String receiverUid;
    private long timestamp;
    private long replyId;
    private String type;
    private int unreadChatCount = 0;

    public Chat() {
    }


    public Chat(String type, String message, String senderUid, String receiverUid, long timestamp,
                int unreadChatCount) {
        this.type = type;
        this.message = message;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.timestamp = timestamp;
        this.unreadChatCount = unreadChatCount;
    }


    public int getUnreadChatCount() {
        return unreadChatCount;
    }

    public void setUnreadChatCount(int unreadChatCount) {
        this.unreadChatCount = unreadChatCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
