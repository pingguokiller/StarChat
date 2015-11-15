package com.nano.starchat2.database;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/2/7.
 */
public class BubblePlayer implements Serializable{
    private static final long serialVersionUID = -758459502806858414L;

    private int id;
    private String name ;
    private double latitude;
    private double lontitude;
    private String headImg;
    private String addr;
    private String sex;

    public BubblePlayer()
    {
    }

    public BubblePlayer(int id, String name, double latitude, double lontitude, String headImg, String sex)
    {
        super();
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.lontitude = lontitude;
        this.headImg = headImg;
        this.sex = sex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public void setLontitude(double lontitude) {
        this.lontitude = lontitude;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
