package com.nano.starchat2.Utils.MessageBean;

/**
 * Created by Administrator on 2015/3/2.
 */
public class Bean_GetMessage {
    private String msgId;
    private String pushType;
    private String msgType;
    private int fromUserId;
    private String fromUserName;
    private int toUserId;
    private String toUserName;
    private String content;
    private long createTime;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }


    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Bean_GetMessage{" +
                "msgId='" + msgId + '\'' +
                ", pushType='" + pushType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", msgId=" + msgId +
                ", fromUserId=" + fromUserId +
                ", fromUserName='" + fromUserName + '\'' +
                ", toUserId=" + toUserId +
                ", toUserName='" + toUserName + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
