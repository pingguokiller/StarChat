package com.nano.starchat2.Utils.JsonBean;

import java.util.List;

/**
 * Created by Administrator on 2015/3/2.
 */
public class Bean_UserList {
    private String code;
    private List<Bean_User> userInfo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Bean_User> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<Bean_User> userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "Bean_UserList{" +
                "code='" + code + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
