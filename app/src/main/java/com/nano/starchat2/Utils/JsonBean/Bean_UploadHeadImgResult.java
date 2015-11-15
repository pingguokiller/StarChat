package com.nano.starchat2.Utils.JsonBean;

/**
 * Created by Administrator on 2015/5/11.
 */
public class Bean_UploadHeadImgResult {
    public String errno;
    public String message;
    public String headImgUrl;

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    @Override
    public String toString() {
        return "Bean_UploadHeadImgResult{" +
                "errno='" + errno + '\'' +
                ", message='" + message + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                '}';
    }
}
