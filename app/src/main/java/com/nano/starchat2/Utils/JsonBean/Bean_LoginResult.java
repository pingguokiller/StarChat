package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/11.
 */
public class Bean_LoginResult {
    public String errno;
    public String info;
    Bean_LoginUserinfo userInfo;

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

    public Bean_LoginUserinfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Bean_LoginUserinfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "Bean_LoginResult{" +
                "errno='" + errno + '\'' +
                ", info='" + info + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
