package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/3/15.
 */
public class Bean_CodeResult {
    private String errno;
    private String info;

    public String getCode() {
        return errno;
    }

    public void setCode(String code) {
        this.errno = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Bean_CodeResult{" +
                "errno='" + errno + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
