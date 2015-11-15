package com.nano.starchat2.Utils.MessageBean;

/**
 * Created by Administrator on 2015/5/17.
 */
public class Bean_SendMessage {
    private String pushType;
    private String msgType;
    private String userId;
    private String content;
    private long createTime;

    public Bean_SendMessage() {
        super();
    }

    public Bean_SendMessage(String pushType, String msgType, String userid, String content, long createTime) {
        this.pushType = pushType;
        this.msgType = msgType;
        this.msgType = userid;
        this.content = content;
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Bean_SendMessage{" +
                "pushType='" + pushType + '\'' +
                ", msgType='" + msgType + '\'' +
                ", userId='" + userId + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
