package com.nano.starchat2.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.baidu.mapapi.map.BaiduMap;
import com.nano.starchat2.Activity.MainActivity;
import com.nano.starchat2.Activity.OfflineActivity;
import com.nano.starchat2.Activity.R;
import com.nano.starchat2.database.BubblePlayer;
import com.nano.starchat2.MainApplication;
import com.nano.starchat2.database.DBManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/7/19.
 * MainAcitivity底部菜单
 */
public class MainacitivityGridMenu{
    MainActivity mainActivity;
    View menuView;
    AlertDialog menuDialog;// menu菜单Dialog
    GridView menuGrid;

    //自定义菜单
    //---------------------------------------------------------------------------------
    private boolean isMore = false;// menu菜单翻页控制
    //AlertDialog menuDialog;// menu菜单Dialog
    //GridView menuGrid;

    //public  View menuView;

    private final int ITEM_SEARCH = 0;// 搜索
    private final int ITEM_FILE_MANAGER = 1;// 文件管理
    private final int ITEM_DOWN_MANAGER = 2;// 下载管理
    private final int ITEM_FULLSCREEN = 3;// 全屏

    private final int ITEM_CHANGEMODE = 5;// 改变地图模式
    private final int ITEM_MORE = 11;// 菜单

    private  Boolean  bMapModeFirst = true;

    /** 菜单图片 **/
    int[] menu_image_array = { R.drawable.menu_search,
            R.drawable.menu_filemanager, R.drawable.menu_downmanager,
            R.drawable.menu_fullscreen, R.drawable.menu_inputurl,
            R.drawable.menu_bookmark, R.drawable.menu_bookmark_sync_import,
            R.drawable.menu_sharepage, R.drawable.menu_quit,
            R.drawable.menu_nightmode, R.drawable.menu_refresh,
            R.drawable.menu_more };
    /** 菜单文字 **/
    String[] menu_name_array = { "搜索", "文件管理", "下载管理", "全屏", "网址", "切换地图",
            "加入书签", "分享页面", "退出", "夜间模式", "刷新", "更多" };
    /** 菜单图片2 **/
    int[] menu_image_array2 = { R.drawable.menu_auto_landscape,
            R.drawable.menu_penselectmodel, R.drawable.menu_page_attr,
            R.drawable.menu_novel_mode, R.drawable.menu_page_updown,
            R.drawable.menu_checkupdate, R.drawable.menu_checknet,
            R.drawable.menu_refreshtimer, R.drawable.menu_syssettings,
            R.drawable.menu_help, R.drawable.menu_about, R.drawable.menu_return };
    /** 菜单文字2 **/
    String[] menu_name_array2 = { "自动横屏", "笔选模式", "阅读模式", "浏览模式", "快捷翻页",
            "检查更新", "检查网络", "定时刷新", "设置", "帮助", "关于", "返回" };
    //---------------------------------------------------------------------------------

    public MainacitivityGridMenu(Context context) {
        this.mainActivity = (MainActivity)context;
    }

    public void init()
    {
        //初邕化menuView
        menuView = View.inflate(mainActivity, R.layout.gridview_menu, null);

        //初始化菜单对话框
        menuDialog = new AlertDialog.Builder(mainActivity).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new DialogInterface.OnKeyListener(){
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
                    dialog.dismiss();
                return false;
            }
        });

        menuGrid = (GridView)menuView.findViewById(R.id.gridview);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        /** 监听menu选项 **/
        menuGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (arg2) {
                    case ITEM_SEARCH:// 搜索
                        MainApplication mapp = (MainApplication) mainActivity.getApplication();
                        DBManager dbManager = mapp.mdbManager;
                        List<BubblePlayer> bubblePlayerList = dbManager.query();
                        BubblePlayer temp = bubblePlayerList.get(1);
                        System.out.println("______________" + temp.getId() + temp.getName() + temp.getLatitude() + temp.getLontitude());
                        break;
                    case ITEM_FILE_MANAGER:// 文件管理
                    {
                        menuDialog.dismiss();         //让菜单界面dialog消失!
                    }
                    break;
                    case ITEM_CHANGEMODE://改变地图模式
                        //开始默认是卫星模式,每按一次这个按钮我们就改变一次标记
                        if (bMapModeFirst) {
                            mainActivity.getmBaiduMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        } else {
                            mainActivity.getmBaiduMap().setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        }
                        bMapModeFirst = (!bMapModeFirst);
                        menuDialog.dismiss();         //让菜单界面dialog消失!
                        break;
                    case ITEM_DOWN_MANAGER:// 下载管理
                        Intent DownLoadIntent = new Intent();
                        DownLoadIntent.setClass(mainActivity, OfflineActivity.class);
                        mainActivity.startActivity(DownLoadIntent);
                        break;
                    case ITEM_FULLSCREEN:// 全屏

                        break;
                    case ITEM_MORE:// 翻页
                        if (isMore) {
                            menuGrid.setAdapter(getMenuAdapter(menu_name_array2,
                                    menu_image_array2));
                            isMore = false;
                        } else {// 首页
                            menuGrid.setAdapter(getMenuAdapter(menu_name_array,
                                    menu_image_array));
                            isMore = true;
                        }
                        menuGrid.invalidate();// 更新menu
                        menuGrid.setSelection(ITEM_MORE);
                        break;
                }
            }
        });
    }

    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
                                         int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(mainActivity, data,
                R.layout.item_menu, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }

    public boolean onProcessMenu(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(mainActivity).setView(menuView).show();
        } else {
            menuDialog.show();

            Window window = menuDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 0.6f;
            window.setAttributes(lp);
        }
        return false;// 返回为true 则显示系统menu
    }
}
