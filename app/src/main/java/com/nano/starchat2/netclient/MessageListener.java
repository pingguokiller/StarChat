package com.nano.starchat2.netclient;

import com.nano.starchat2.Utils.MessageBean.TranObject;

/**
 * 消息监听接口
 * 
 * @author way
 * 
 */
public interface MessageListener {
	public void Message(TranObject msg);
}
