package com.nano.starchat2.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.google.gson.Gson;
import com.nano.starchat2.MainApplication;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.MessageBean.Bean_GetMessage;
import com.nano.starchat2.Utils.MessageBean.Bean_SendLoginInfo;
import com.nano.starchat2.Utils.MessageBean.TextViewUnit;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * 收取消息服务
 * 
 * @author way
 * 
 */
public class GetMsgService extends Service {
	private static final int MSG = 0x001;
	private MainApplication application;
	//private Client client;
	private boolean isStart = false;// 是否与服务器连接上
	private Context mContext = this;

    //webSocket聊天信息链接路径
    public static String      wsUrl   = ConstantsUtil.WEBSOCKET_IP + ConstantsUtil.SERVER_PORT;
    public WebSocketConnection wsC;

    private android.os.Handler mHandlerSend;
    private Runnable ReConnectThread;

    //长连接线程
    private WebSocketConnectionHandler wcHandler;

    //清楚TextView队列
    private android.os.Handler mHandlerClearTextView;
    private Runnable  clearTextViewThread;

    private OnMessageDataListener mMessageDataListener;
	@Override
	public IBinder onBind(Intent intent) {
        return new MsgBinder();
	}

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public GetMsgService getService(){
            return GetMsgService.this;
        }
    }

    public void setOnMessageDataListener(OnMessageDataListener onMessageDataListener) {
        this.mMessageDataListener = onMessageDataListener;
    }

    @Override
	public void onCreate() {// 在onCreate方法里面注册广播接收者
		// TODO Auto-generated method stub
		super.onCreate();

        mHandlerSend = new Handler();
        mHandlerClearTextView = new Handler();

        wsC = new WebSocketConnection();
        wcHandler = new WebSocketConnectionHandler() {
            @Override
            public void onOpen()
            {
                System.out.print("Status: Connected to " + wsUrl);

                //先看是否登录成功
                SharedPreferences preferences = getSharedPreferences(
                        ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
                Boolean tempIsBoolen = preferences.getBoolean("IsLogin", false);

                //如果还没有登录成功， 我们要先登录
                if (tempIsBoolen)
                {
                    //发送userInfo
                    String userId = preferences.getString("userid","");
                    String username = preferences.getString("username","");
                    Bean_SendLoginInfo tempSendLoginInfo = new Bean_SendLoginInfo(ConstantsUtil.PUSHTYPE_LOGIN, userId,username);

                    String sendJson = new Gson().toJson(tempSendLoginInfo);
                    wsC.sendTextMessage(sendJson);
                }
            }

            @Override
            public void onTextMessage( String payload )
            {
                System.out.println( "Got echo: " + payload );

                try {
                    Bean_GetMessage AllResult = new Gson().fromJson(payload, Bean_GetMessage.class);
                    if (null != AllResult) {
                        mMessageDataListener.OnDataArrive(AllResult);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onClose( int code, String reason )
            {
                System.out.print( "Connection lost." );
            }
        };

        application = (MainApplication) this.getApplicationContext();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
        try {
            wsC.connect( wsUrl, wcHandler);
        } catch ( WebSocketException e ) {
            e.printStackTrace();
        }

        ReConnectThread = new Runnable() {
            @Override
            public void run() {
                System.out.println("send message:");

                if (wsC.isConnected()) {
                    /*
                    Bean_SendMessage tempSendMsg = new Bean_SendMessage();
                    tempSendMsg.setMsgType(ConstantsUtil.MSGTYPE_CHATMSG);
                    tempSendMsg.setPushType(ConstantsUtil.PUSHTYPE_CHATMSG);
                    tempSendMsg.setUserId("18");
                    tempSendMsg.setContent("123456");
                    tempSendMsg.setCreateTime(System.currentTimeMillis());

                    String sendJson = new Gson().toJson(tempSendMsg);
                    SendMessage(sendJson);
                    */
                }
                else {
                    //没连上了就重连
                    System.out.print("not connect!");
                    try {
                        wsC.connect( wsUrl, wcHandler);
                    } catch ( WebSocketException e ) {
                        e.printStackTrace();
                    }
                }
                mHandlerSend.postDelayed(ReConnectThread, 10000);
            }
        };
        mHandlerSend.post(ReConnectThread);


        clearTextViewThread = new Runnable() {
            @Override
            public void run() {
                TextViewUnit tempUnit = new TextViewUnit();
                tempUnit = application.gTextViewQueue.peek();

                if (null == tempUnit)
                {
                    //当前 队列未空
                    int i = 0;
                }
                else {
                    long timeInt = System.currentTimeMillis() - tempUnit.getCreateTime();
                    if ( timeInt > 10000) {
                        System.out.println("GetMsgService: delete a View from the View！");
                        tempUnit.getView().clearAnimation();
                        application.gBaiduMapView.removeView(tempUnit.getView());
                        application.gBaiduMapView.refreshDrawableState();

                        application.gTextViewQueue.poll();
                    }
                }
                mHandlerClearTextView.postDelayed(clearTextViewThread, 5000);
            }
        };
        mHandlerClearTextView.post(clearTextViewThread);
	}

    //发送消息接口函数
    public boolean SendMessage(String msg)
    {
        if (wsC.isConnected())
        {
            wsC.sendTextMessage(msg);
            return true;
        }
        else
        {

            return false;
        }
    }

	@Override
	// 在服务被摧毁时，做一些事情
	public void onDestroy() {
		super.onDestroy();

        if (wsC.isConnected()) {
            wsC.disconnect();
        }
		// 给服务器发送下线消息
		if (isStart) {

		}
	}
}
