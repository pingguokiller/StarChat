package com.nano.starchat2.Utils.MessageBean;

/**
 * Created by Administrator on 2015/5/24.
 */
public class Bean_SendLoginInfo {
    private String pushType;
    private String userId;
    private String userName;

    public Bean_SendLoginInfo(String pushType, String userId ,String username) {
        this.pushType = pushType;
        this.userId = userId;
        this.userName = username;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Bean_SendLoginInfo{" +
                "pushType='" + pushType + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
