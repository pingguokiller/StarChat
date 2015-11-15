package com.nano.starchat2.Listener;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.nano.starchat2.Activity.MainActivity;
import com.nano.starchat2.Activity.UserInfoActivity;

/**
 * Created by Administrator on 2015/7/19.
 *
 */
public class MainActivityUsermenuListviewListener implements AdapterView.OnItemClickListener{
    MainActivity mainActivity;

    public MainActivityUsermenuListviewListener( MainActivity activity )
    {
        mainActivity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //如果点击的是第一行　"个人资料"
        if(0 == position)
        {
            Intent intent = new Intent(mainActivity, UserInfoActivity.class);
            mainActivity.startActivity(intent);
        }
    }
}
