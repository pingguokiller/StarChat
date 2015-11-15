package com.nano.starchat2;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.nano.starchat2.Utils.MessageBean.TextViewUnit;
import com.nano.starchat2.database.DBManager;
import com.nano.starchat2.netclient.Client;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Administrator on 2015/2/3.
 */
public class MainApplication extends Application {
    public DBManager mdbManager;

    //聊天信息声明未全局的
    public Queue<TextViewUnit> gTextViewQueue;
    public MapView gBaiduMapView;
    //public MainActivity mainActivity;

    public int playerNum = 0;
    public int playerNum1 = 0;

    private Client client;// 客户端
    private boolean isClientStart;// 客户端连接是否启动
    @Override
    public void onCreate() {
        super.onCreate();
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        gTextViewQueue = new LinkedList<TextViewUnit>();
        //mainActivity = new MainActivity();

        //初始化DBManager
        mdbManager = new DBManager(this);
    }

    public DBManager getMdbManager() {
        return mdbManager;
    }

    public Client getClient() {
        return client;
    }

    public boolean isClientStart() {
        return isClientStart;
    }

    public void setClientStart(boolean isClientStart) {
        this.isClientStart = isClientStart;
    }
}
