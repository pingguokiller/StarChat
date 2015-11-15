package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/3/15.
 */
public class Bean_Status {
    private String status;
    private String info;

    @Override
    public String toString() {
        return "Bean_Status{" +
                "status='" + status + '\'' +
                ", info='" + info + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
