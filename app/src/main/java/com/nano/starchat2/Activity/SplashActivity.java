package com.nano.starchat2.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nano.starchat2.AsynTask.LoginTask;
import com.nano.starchat2.Utils.ConstantsUtil;

/**
 *
 * @{# SplashActivity.java Create on 2013-5-2 下午9:10:01
 *
 *     class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 *     (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 *
 *     <p>
 *     Copyright: Copyright(c) 2013
 *     </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 *
 *
 */
public class SplashActivity extends Activity {
    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    // 延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 500;

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        init();
    }

    private void init() {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, MODE_MULTI_PROCESS);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);

        //fortest 每次都进入欢饮界面
        isFirstIn = true;
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (!isFirstIn) {
            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }
    }

    //如果已经不是第一次进入APP了，我们就去登录一次，无论登录是否成功，我们都会进入 主界面
    private void goHome() {
        //fortest
        SharedPreferences preferences1 = getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = preferences1.edit();
        // 存入数据
        //editor.putString("username", "");
        //editor.putString("password", "");
       // editor.putString("userid", "");
        editor.putBoolean("IsLogin", false);
        // 提交修改
        editor.commit();

        //添加登录
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, MODE_MULTI_PROCESS);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        String username = preferences.getString("username", "");
        String password= preferences.getString("password", "");

        if (username.equals("")) {
            //是第一次　帮助窗口
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();
        }
        else {
            LoginTask loginTask = new LoginTask(SplashActivity.this, username, password);
            loginTask.execute();
        }
    }

    private void goGuide() {
        //是第一次　帮助窗口
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}
