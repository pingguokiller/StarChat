package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/3/15.
 */
public class Bean_RegResult {
    String errno;
    String info;
    BeanUserInfo userInfo;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

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
        return "Bean_RegResult{" +
                "errno='" + errno + '\'' +
                ", info='" + info + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
