package com.nano.starchat2.netclient;

import com.nano.starchat2.Utils.MessageBean.TranObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * 客户端读消息线程
 * 
 * @author way
 * 
 */
public class ClientInputThread extends Thread {
	private Socket socket;
	private TranObject msg;
	private boolean isStart = true;
	private ObjectInputStream ois;
	private MessageListener messageListener;// 消息监听接口对象

	public ClientInputThread(Socket socket) {
		this.socket = socket;

        int i =0;
	}

	/**
	 * 提供给外部的消息监听方法
	 * 
	 * @param messageListener
	 *            消息监听接口对象
	 */
	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public void run() {
        //在线程中创建ObjectInputStream　使得线程不阻塞
        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
		try {
			while (isStart) {
				msg = (TranObject) ois.readObject();
				// 每收到一条消息，就调用接口的方法，并传入该消息对象，外部在实现接口的方法时，就可以及时处理传入的消息对象了
				// 我不知道我有说明白没有？
				messageListener.Message(msg);
                System.out.print("+++++++++++++++++++收到了一条消息++++++++++++++++++++++");
			}
			ois.close();
			if (socket != null)
				socket.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
