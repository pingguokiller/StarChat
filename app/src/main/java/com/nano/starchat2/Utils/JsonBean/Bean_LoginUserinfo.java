package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/16.
 */
public class Bean_LoginUserinfo {
    public String user_id;
    public String user_name;
    //public String nickname;
    public String real_name;
    public String head_img;
    public String identity_cart;
    public String you_lines;
    public String sex;
    public String birthday;
    public String nation;
    public String region;
    public String city;
    public String email;
    public String mobile;
    public String addtime;

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

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getIdentity_cart() {
        return identity_cart;
    }

    public void setIdentity_cart(String identity_cart) {
        this.identity_cart = identity_cart;
    }

    public String getYou_lines() {
        return you_lines;
    }

    public void setYou_lines(String you_lines) {
        this.you_lines = you_lines;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    /*
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }*/

    @Override
    public String toString() {
        return "Bean_LoginUserinfo{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                //", nickname='" + nickname + '\'' +
                ", real_name='" + real_name + '\'' +
                ", head_img='" + head_img + '\'' +
                ", identity_cart='" + identity_cart + '\'' +
                ", you_lines='" + you_lines + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", nation='" + nation + '\'' +
                ", region='" + region + '\'' +
                ", city='" + city + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", addtime='" + addtime + '\'' +
                '}';
    }
}
