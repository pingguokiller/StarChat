package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/16.
 */
public class BeanUserInfo {
    String user_id;
    String user_name;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "BeanUserInfo{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
