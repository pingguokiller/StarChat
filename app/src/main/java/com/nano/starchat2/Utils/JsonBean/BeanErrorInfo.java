package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/16.
 */
public class BeanErrorInfo extends BeanInfoBase {
    String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "BeanErrorInfo{" +
                "info='" + info + '\'' +
                '}';
    }
}
