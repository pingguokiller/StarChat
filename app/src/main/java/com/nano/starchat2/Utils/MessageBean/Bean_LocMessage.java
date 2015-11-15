package com.nano.starchat2.Utils.MessageBean;

/**
 * Created by Administrator on 2015/5/17.
 */
public class Bean_LocMessage {
    private String pushType;
    private String userId;
    private double latitude;
    private double longitude;
    private long createTime;

    public Bean_LocMessage(String pushType, String userid, double latitude, double longitude, long createTime) {
        this.pushType = pushType;
        this.userId = userid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createTime = createTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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
        return "Bean_LocMessage{" +
                "pushType='" + pushType + '\'' +
                ", userId='" + userId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createTime=" + createTime +
                '}';
    }
}
