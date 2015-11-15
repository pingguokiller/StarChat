package com.nano.starchat2.Service;

import com.nano.starchat2.Utils.MessageBean.Bean_GetMessage;

/**
 * Created by Administrator on 2015/4/12.
 */
public interface OnMessageDataListener {
    void OnDataArrive(Bean_GetMessage msg);
}
