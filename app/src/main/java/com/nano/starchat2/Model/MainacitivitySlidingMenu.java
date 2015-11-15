package com.nano.starchat2.Model;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nano.starchat2.Activity.MainActivity;
import com.nano.starchat2.Activity.R;
import com.nano.starchat2.Listener.MainActivityUsermenuListviewListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/19.
 * MainAcitivity左侧滑菜单
 */
public class MainacitivitySlidingMenu extends SlidingMenu{
    MainActivity mainActivity;

    private ListView userMenulistView;

    public MainacitivitySlidingMenu(Context context) {
        super(context);
        mainActivity = (MainActivity)context;;
    }

    public MainacitivitySlidingMenu(Activity activity, int slideStyle) {
        super(activity, slideStyle);
        mainActivity = (MainActivity)activity;
    }

    public MainacitivitySlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainActivity = (MainActivity)context;
    }

    public MainacitivitySlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mainActivity = (MainActivity)context;
    }

    public void init()
    {
        this.setMode(SlidingMenu.LEFT);  //SlidingMenu().toggle()
        this.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        this.setShadowWidthRes(R.dimen.shadow_width);
        this.setShadowDrawable(R.drawable.shadow);
        this.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        this.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        this.attachToActivity(mainActivity, SlidingMenu.SLIDING_CONTENT);
        this.setMenu(R.layout.userinfo_menu_frame);

        //初始化ListView
        initMenuListView();
    }

    private void initMenuListView()
    {

        userMenulistView = (ListView)findViewById(R.id.userInfo_menu_ListView);

        userMenulistView.setAdapter(new SimpleAdapter(mainActivity, getData(),R.layout.userinfo_menu_listitem, new String[]{"icon","name","tip"}, new int[]{R.id.menuItemIcon,R.id.menuItemName,R.id.menuItemTip}));

        userMenulistView.setOnItemClickListener(new MainActivityUsermenuListviewListener(mainActivity));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_newsfeed);
        map.put("name", "个人资料");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_message);
        map.put("name", "Messages");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_friends);
        map.put("name", "Friends");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_photos);
        map.put("name", "Photos");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_events);
        map.put("name", "Events");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("icon", R.drawable.usermenu_icon_groups);
        map.put("name", "Groups");
        map.put("tip", R.drawable.usermenu_tip);
        list.add(map);

        return list;
    }
}
