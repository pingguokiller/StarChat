package com.nano.starchat2.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.gson.Gson;
import com.nano.starchat2.AsynTask.DrawBubbleTask;
import com.nano.starchat2.MainApplication;
import com.nano.starchat2.Model.CircleImageView;
import com.nano.starchat2.Model.MainacitivityGridMenu;
import com.nano.starchat2.Model.MainacitivitySlidingMenu;
import com.nano.starchat2.Service.GetMsgService;
import com.nano.starchat2.Service.OnMessageDataListener;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.DistanceUtil;
import com.nano.starchat2.Utils.MessageBean.Bean_GetMessage;
import com.nano.starchat2.Utils.MessageBean.Bean_LocMessage;
import com.nano.starchat2.Utils.MessageBean.Bean_SendMessage;
import com.nano.starchat2.Utils.MessageBean.TextViewUnit;
import com.nano.starchat2.Utils.NetworkUtil;
import com.nano.starchat2.database.BubblePlayer;
import com.nano.starchat2.database.DBManager;

import java.util.HashMap;
import java.util.Map;

import cn.trinea.android.common.service.impl.ImageCache;

public class MainActivity extends Activity  implements View.OnClickListener,
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

    //手机经纬度，如果手机经位置变化超过了10m，那么我们就上报
    public double m_dLatitude = 0;
    public double m_dlongitude = 0;

    //地图视图
    private MapView mMapView;
    //百度地图类
    private BaiduMap mBaiduMap;

    //百度地图　搜索相关类
    private PoiSearch mPoiSearch;
    private SuggestionSearch mSuggestionSearch;
    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView;
    private ArrayAdapter<String> sugAdapter;
    BDLocation m_Location;

    //创建地图Ｍark 自已的头像
    private ImageView ivMarker;

    //UiSettings
    private UiSettings mUiSetting;
    //绑定GetMsgservice
    private GetMsgService getMsgService;

    //图片缓存
    private ImageCache m_IMAGE_CACHE;

    /*放大缩小按钮
    private Button zoomInBtn;
    private Button zoomOutBtn;*/

    private Button userinfoManageBtn;  //用户界面管理按钮
    private Button chatFunctionBtn;  //用户界面管理按钮

    private Button sendMsgActionBtn;  //发送按钮
    private Button sendVoiceMsgActionBtn;//声音
    private EditText sendMsgEdit;

    // 定位相关
    //定位客户端
    LocationClient mLocClient;
    //自定义监听器
    public MyLocationListenner myListener = new MyLocationListenner(this);
    //定位模式
    private MyLocationConfiguration.LocationMode mCurrentMode;

    //百度地图中的标记
    BitmapDescriptor mCurrentMarker;

    // 是否首次定位标记
    boolean isFirstLoc = true;

    //获取BaiduView视图区域的长度和宽度
    private int mScreenWidth = -1;
    private int mScreenHeight = -1;
    private int mStatusHeight = -1;  //状态栏 高度
    private int mTitleHeight = -1;   //标题栏 高度

    private Map<Integer, BubblePlayer> DrawedBubbleMap;
    private Map<Integer, ImageView> DrawedViewMap;


    //侧滑菜单界面
    private MainacitivitySlidingMenu m_Slidingmenu;

    //底部菜单
    private MainacitivityGridMenu m_gridMenu;

    @Override
    //创建activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置没有标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //绑定服务
        initMsgService();

        //初始化mMapView，mBaiduMap
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);//隐藏缩放控件

        ((MainApplication)(getApplication())).gBaiduMapView = mMapView;

        //声明BaiduMap类
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        /*获取缩放控件
        zoomInBtn = (Button)findViewById(R.id.zoomin);
        zoomOutBtn = (Button)findViewById(R.id.zoomout);
        sendMsgBtn = (Button)findViewById(R.id.SendMessageButton);

        zoomInBtn.setOnClickListener(this);
        zoomOutBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
        */

        //左上角头像按钮
        userinfoManageBtn = (Button)findViewById(R.id.UserInfoButton);

        //右上角聊天功能按钮
        chatFunctionBtn = (Button)findViewById(R.id.ChatFunctionButton);
        chatFunctionBtn.setOnClickListener(this);

        sendMsgEdit = (EditText)findViewById(R.id.SendMessageEditText);
        sendMsgActionBtn = (Button)findViewById(R.id.SendMsgActionButton);
        sendVoiceMsgActionBtn = (Button)findViewById(R.id.SendVoiceMsgActionButton);

        userinfoManageBtn.setOnClickListener(this);
        sendMsgActionBtn.setOnClickListener(this);
        sendMsgEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //当输入edit为空时
                /*if(sendMsgEdit.getText().length() <= 0)
                {
                    sendMsgActionBtn.setVisibility(View.INVISIBLE);
                }
                else
                {
                    sendMsgActionBtn.setVisibility(View.VISIBLE);
                }*/
            }
        });

        //定位模式 默认为普通，还可以有跟踪罗盘
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        //创建地图Ｍark
        ivMarker = new CircleImageView(this);

        //创建BitMap对象，用于显示图片
        //从本地读取头像，并显示
        Bitmap bmp = BitmapFactory.decodeFile(ConstantsUtil.MYHEADDIR);
        if (null == bmp)
        {
            bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.defaulthead);
        }

        int primaryWidth = bmp.getWidth();
        int primaryHeight = bmp.getHeight();

        Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
        float scaleWidth = (float)55/primaryWidth;
        float scaleHeight = scaleWidth;//(float)80/primaryHeight;

        matrix.postScale(scaleWidth, scaleHeight);    //设置高宽比例（三维矩阵）

        //缩放后的BitMap
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

        //重新设置BitMap
        ivMarker.setImageBitmap(newBmp);

        //自定义定位标记 默认为蓝色小球
        mCurrentMarker = BitmapDescriptorFactory
                .fromView(ivMarker);

        /*BitmapDescriptor	customMarker                用户自定义定位图标
        boolean	            enableDirection             是否允许显示方向信息
        MyLocationConfiguration.LocationMode	locationMode        定位图层显示方式*/
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, false, mCurrentMarker));



        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //定位初始化
        initLocation();

        //设置地图状态改变完成 监听器
        SetMapStatusFinish();

        //声明 已绘画泡 Map
        DrawedBubbleMap = new HashMap<>();
        DrawedViewMap = new HashMap<>();

        //设定Marker的监听器
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(final Marker marker)
            {
                //空
                return true;
            }
        });

        //设置不显示指南针
        mUiSetting = mBaiduMap.getUiSettings();
        mUiSetting.setCompassEnabled(false);

        //创建底部菜单
        m_gridMenu = new MainacitivityGridMenu(this);
        m_gridMenu.init();

        //左侧用户管理界面划出菜单
        m_Slidingmenu = new MainacitivitySlidingMenu(this);
        m_Slidingmenu.init();

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.SearchEidtTxt);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                //String city = ((EditText) findViewById(R.id.city)).getText()
                        //.toString();
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                String city = m_Location.getCity();
                if(!city.equals("")) {
                    mSuggestionSearch
                            .requestSuggestion((new SuggestionSearchOption())
                                    .keyword(cs.toString()).city(city));
                }
            }
        });
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                //fortest
                String str;
                str = info.key;
                sugAdapter.add(info.key);
            }
        }
        sugAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(MainActivity.this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(MainActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //绑定获取消息服务
    private void initMsgService()
    {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, GetMsgService.class);
        boolean bRet = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        if(!bRet) {
            int i = 0;
        }
    }
    //定位初始化
    private void initLocation()
    {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型 这是百度加密经纬坐标
        option.setScanSpan(1000);//1秒钟得到一次，调试设短一些，后期可以加长，
        option.setAddrType("all");
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    //设置头像
    private void  setHeadIconView()
    {
        /*
        //创建BitMap对象，用于显示图片
        //从本地读取头像，并显示
        Bitmap bmp = BitmapFactory.decodeFile(ConstantsUtil.MYHEADDIR);
        if (null == bmp)
        {
            bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.defaulthead);
        }

        int primaryWidth = bmp.getWidth();
        int primaryHeight = bmp.getHeight();

        Matrix matrix = new Matrix();   //矩阵，用于图片比例缩放
        float scaleWidth = (float)55/primaryWidth;
        float scaleHeight = scaleWidth;//(float)80/primaryHeight;

        matrix.postScale(scaleWidth, scaleHeight);    //设置高宽比例（三维矩阵）

        //缩放后的BitMap
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, primaryWidth, primaryHeight, matrix, true);

        //重新设置BitMap
        ivMarker.setImageBitmap(newBmp);
        */




        //左侧滑出菜单里面的头像
        CircleImageView circleHeadView =  (CircleImageView)findViewById(R.id.userInfo_menu_headicon_id);

        //从本地读取头像，并显示
        Bitmap bmpLeft = BitmapFactory.decodeFile(ConstantsUtil.MYHEADDIR);
        if (null == bmpLeft)
        {
            bmpLeft = BitmapFactory.decodeResource(this.getResources(), R.drawable.defaulthead);
        }
        circleHeadView.setImageBitmap(bmpLeft);

        TextView textView = (TextView)findViewById(R.id.userInfo_menu_headName_id);
        textView.setText("张楗伟");
    }

    //当在输入框输入信息的时候，输入法会弹出来，点击其他地方，会使输入法消失
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        //login_auto_account.setCursorVisible(false);
        imm.hideSoftInputFromWindow(sendMsgEdit.getWindowToken(), 0);
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId())
       {
           //点击右上角聊天功能键
           case R.id.ChatFunctionButton:
           {
               String city = m_Location.getCity();
               if (!city.equals("")) {
                   EditText editSearchKey = (EditText) findViewById(R.id.SearchEidtTxt);
                   mPoiSearch.searchInCity((new PoiCitySearchOption())
                           .city(city)
                           .keyword(editSearchKey.getText().toString())
                           .pageNum(0));
               }
           }
           break;
           //点击淡出 发送消息编辑框 按钮
           /*case R.id.SendMessageButton:
           {
               sendMsgEdit.setVisibility(View.VISIBLE);
               sendVoiceMsgActionBtn.setVisibility(View.VISIBLE);
           }
           break;*/
           //点击发送消息按钮
           case R.id.SendMsgActionButton:
           {
               //先看是否登录成功
               SharedPreferences preferences = this.getSharedPreferences(
                       ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
               Boolean tempIsBoolen = preferences.getBoolean("IsLogin", false);
               //如果还没有登录成功， 我们要先登录
               if (!tempIsBoolen)
               {
                   //无论是否成功，我们都会进入主界面
                   Intent intent = new Intent(this, LoginActivity.class);
                   this.startActivity(intent);

                   return;
               }

               String tempUserid = preferences.getString("userid", "");
               //String tempUserName = preferences.getString("username", "");
               //发送消息
               String msg = sendMsgEdit.getText().toString();
               if(msg.equals(""))
               {
                   return;
               }

               Bean_SendMessage tempSendMsg = new Bean_SendMessage();
               tempSendMsg.setMsgType(ConstantsUtil.MSGTYPE_CHATMSG);
               tempSendMsg.setPushType(ConstantsUtil.PUSHTYPE_CHATMSG);
               tempSendMsg.setUserId(tempUserid);
               tempSendMsg.setContent(msg);
               tempSendMsg.setCreateTime(System.currentTimeMillis());

               String sendJson = new Gson().toJson(tempSendMsg);
               getMsgService.SendMessage(sendJson);
               sendMsgEdit.setText("");

               //输入后通过设置焦点隐藏输入法
               InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
               //login_auto_account.setCursorVisible(false);
               imm.hideSoftInputFromWindow(sendMsgEdit.getWindowToken(), 0);

               //自己发送完信息之后，要在自己的坐标处打印聊天泡泡信息，表示自己发送了信息。
               MyLocationData locData = mBaiduMap.getLocationData();

               //生成一个TextView用户在地图中显示InfoWindow
               TextView location = new TextView(getApplicationContext());
               location.setBackgroundResource(R.drawable.incoming3);
              // location.setPadding(20, 20, 20, 20);
//                        location.setMaxWidth(300);
//                        location.setMinWidth(100);
//                        location.setMaxHeight(200);
//                        location.setMinHeight(50);
               //location.getBackground().setAlpha(100);
               location.setTextColor(R.color.black);
               location.setText(msg);

               //将marker所在的经纬度的信息转化成屏幕上的坐标
               final LatLng latLng = new LatLng(locData.latitude, locData.longitude);
               Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
               //要说的话的位置的设定

               LatLng llInfotest = mBaiduMap.getProjection().fromScreenLocation(p);

               int tmpLen = location.length();

               p.y -= 50;
               p.x += ConstantsUtil.TEXTWIDTHUNIT*location.length()/2;
               LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);

               MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder().
                       layoutMode(MapViewLayoutParams.ELayoutMode.mapMode).position(llInfo);
               MapViewLayoutParams mapViewLayoutPara = builder.build();

               //location.setImageResource(R.drawable.bubble_red);
               Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.chat_out);
               location.setAnimation(anim);

               //将View信息保存到全局队列中
               TextViewUnit tempUnit = new TextViewUnit();
               tempUnit.setCreateTime( System.currentTimeMillis() );
               tempUnit.setView(location);

               ((MainApplication) getApplication()).gTextViewQueue.offer(tempUnit);

               //将Ｖiew放入百度地图ＶiewGroup， 此处还没有考虑定时将这些TextView remove掉，留待后面去做
               mMapView.addView(location, mapViewLayoutPara);
               mMapView.refreshDrawableState();
           }
           break;
           /*case R.id.zoomin:
           {
               float zoomLevel = mBaiduMap.getMapStatus().zoom;
               if(zoomLevel<=20){
					//MapStatusUpdateFactory.zoomIn();
                   mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                   zoomOutBtn.setEnabled(true);
               }else{
                   //Toast.makeText(getActivity(), "已经放至最大！", Toast.LENGTH_SHORT).show();
                   zoomInBtn.setEnabled(false);
               }
           }
           break;
           case R.id.zoomout:
           {
               float zoomLevel = mBaiduMap.getMapStatus().zoom;
               if(zoomLevel>4){
                   mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                   zoomInBtn.setEnabled(true);
               }else{
                   zoomOutBtn.setEnabled(false);
                   // Toast.makeText(getActivity(), "已经缩至最小！", Toast.LENGTH_SHORT).show();
               }
           }
           break;*/
           //点击用户管理　按钮
           case R.id.UserInfoButton:
           {
               //侧面菜单滑出
               m_Slidingmenu.toggle();
           }
           break;
           default:
           {
           }
           break;
       }
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //菜单处理
        m_gridMenu.onProcessMenu(featureId, menu);
        return false;// 返回为true 则显示系统menu
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mMapView.onResume();

        //启动获取消息服务
        if (NetworkUtil.isNetworkAvailable(this)) {
            Intent service = new Intent(MainActivity.this, GetMsgService.class);
            startService(service);
        } else {
            NetworkUtil.toast(this);
        }

        //设置头像 左测滑出框的头像
        setHeadIconView();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);

        //释放搜索相关类
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.onDestroy();
        super.onDestroy();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        private Context context;
        public MyLocationListenner(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            //更新当前地理位置
            m_Location = location;

            //先看是否登录成功
            SharedPreferences preferences = ((Activity)context).getSharedPreferences(
                    ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
            String tempUserid = preferences.getString("userid", "");

            //如果还没有登录成功， 我们要先登录
            if (!tempUserid.equals(""))
            {
                double tempDistance = DistanceUtil.GetDistance(location.getLatitude(), location.getLongitude(),
                        ((MainActivity)context).m_dLatitude, ((MainActivity)context).m_dlongitude);

                //如果差距大于10m，我才上报一次位置 信息，不然就不报
                if (tempDistance > 10)
                {
                    //发送消息
                    Bean_LocMessage tempLocMsg = new Bean_LocMessage(ConstantsUtil.PUSHTYPE_LOCINFO ,
                            tempUserid,location.getLatitude(), location.getLongitude(), System.currentTimeMillis());

                    String sendJson = new Gson().toJson(tempLocMsg);
                    getMsgService.SendMessage(sendJson);

                    ((MainActivity)context).m_dLatitude = location.getLatitude();
                    ((MainActivity)context).m_dlongitude = location.getLongitude();
                }
            }

            //打印位置信息
            LogLocation(location);

            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                     // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            //如果是第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                /*以动画方式更新地图状态
                参数:
                update - 地图状态将要发生的变化
                durationMs - 动画时间 = 300ms*/
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    //打印位置信息
    public void LogLocation(BDLocation location){
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation){
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\ndirection : ");
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //sb.append(location.getDirection());
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            //sb.append(location.getOperators());
        }
    }

    //设置地图状态改变 监听器
    public  void SetMapStatusFinish(){
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //如果是第一次获取屏幕尺寸的话
                if( mTitleHeight == -1)
                {
                    GetScreenStatusTitleRect();
                }

                //对状态栏高度进行判断
                if( (mStatusHeight < 0) || (mStatusHeight > (0.5*mScreenHeight)) ){
                    return;
                }

                //对标题栏高度进行判断
                if( (mTitleHeight < 0) || (mTitleHeight > (0.5*mScreenHeight)) ){
                    return;
                }

                //左上角坐标
                Point pointLeftUp = new Point(0, mStatusHeight+mTitleHeight);
                //右上角坐标
                //pointRightUp = new Point(mScreenWidth, mStatusHeight+mTitleHeight);
                //左下角坐标
                //Point pointLeftDown = new Point(0, mScreenHeight);
                //右下角坐标
                Point pointRightDown = new Point(mScreenWidth, mScreenHeight);

                //获取坐上角和右下角的经纬坐标
                LatLng tempLatLngLeftUp =  mBaiduMap.getProjection().fromScreenLocation(pointLeftUp);
                LatLng tempLatLngRightDown =  mBaiduMap.getProjection().fromScreenLocation(pointRightDown);

                //根据左下角和右下角的坐标范围来画泡泡
                DrawRectBubble(tempLatLngLeftUp.latitude, tempLatLngLeftUp.longitude, tempLatLngRightDown.latitude, tempLatLngRightDown.longitude);
            }
        });
    }

    //获取屏幕高度宽度属性
    private  void GetScreenStatusTitleRect()
    {
        //视图的长和高我们直接得到存下来，后面我们直接调用，避免反复调用
        //屏幕宽度和高度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;     // 屏幕宽度（像素）
        mScreenHeight = metric.heightPixels;   // 屏幕高度（像素）
        //float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        //int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        //状态栏宽度和高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        mStatusHeight = frame.top;

        //标题栏高度
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //statusBarHeight是上面所求的状态栏的高度
        mTitleHeight = contentTop - mStatusHeight;
    }

    //画出一定区域内的泡泡
    private  void DrawRectBubble(double LeftUpLat, double LeftUpLon, double RightDownLat, double RightDownLon)
    {
        DrawBubbleTask drawBubbleTask = new DrawBubbleTask(this, LeftUpLat,  LeftUpLon,  RightDownLat, RightDownLon);
        drawBubbleTask.execute();
    }

    public Map<Integer, BubblePlayer> getDrawedBubbleMap() {
        return DrawedBubbleMap;
    }

    //Getter
    public MapView getmMapView() {
        return mMapView;
    }

    public Map<Integer, ImageView> getDrawedViewMap() {
        return DrawedViewMap;
    }

    public BaiduMap getmBaiduMap() {
        return mBaiduMap;
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            getMsgService = ((GetMsgService.MsgBinder)service).getService();

            //注册回调接口来接收从网络上受到的消息
            getMsgService.setOnMessageDataListener(new OnMessageDataListener() {
                @Override
                public void OnDataArrive(Bean_GetMessage msg) {
                    //根据msg中的fromUser知道是谁说的。然后显示到那个fromUser身上
                    DBManager tempDBManager = ((MainApplication)(getApplication())).mdbManager;
                    BubblePlayer tempPlayer = new BubblePlayer();
                    //tempPlayer = tempDBManager.query(msg.getFromUserId());
                    //从异化泡泡上找
                    tempPlayer = getDrawedBubbleMap().get(msg.getFromUserId());

                    if (null == tempPlayer) {
                        return;
                    }
                    //生成一个TextView用户在地图中显示InfoWindow
                    TextView location = new TextView(getApplicationContext());
                    location.setBackgroundResource(R.drawable.incoming3);
                    location.setPadding(20, 20, 20, 20);
//                        location.setMaxWidth(300);
//                        location.setMinWidth(100);
//                        location.setMaxHeight(200);
//                        location.setMinHeight(50);
                    location.getBackground().setAlpha(100);
                    location.setText(msg.getContent());

                    //将marker所在的经纬度的信息转化成屏幕上的坐标
                    final LatLng ll = new LatLng(tempPlayer.getLatitude(), tempPlayer.getLontitude());
                    Point p = mBaiduMap.getProjection().toScreenLocation(ll);
                    //要说的话的位置的设定
                    p.y -= 100;
                    LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);

                    MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder().layoutMode(MapViewLayoutParams.ELayoutMode.mapMode).position(ll);
                    MapViewLayoutParams mapViewLayoutPara = builder.build();

                    //location.setImageResource(R.drawable.bubble_red);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.chat_out);
                    location.setAnimation(anim);

                    //将View信息保存到全局队列中
                    TextViewUnit tempUnit = new TextViewUnit();
                    tempUnit.setCreateTime( System.currentTimeMillis() );
                    tempUnit.setView(location);

                    ((MainApplication) getApplication()).gTextViewQueue.offer(tempUnit);

                    //将Ｖiew放入百度地图ＶiewGroup， 此处还没有考虑定时将这些TextView remove掉，留待后面去做
                    mMapView.addView(location, mapViewLayoutPara);
                    mMapView.refreshDrawableState();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


}
