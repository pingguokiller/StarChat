package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/16.
 */
public class BeanSuccessInfo extends BeanInfoBase{
    String info;

    BeanUserInfo userInfo;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public BeanUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(BeanUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "BeanSuccessInfo{" +
                "info='" + info + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
