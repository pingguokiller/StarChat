package com.nano.starchat2.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2015/3/22.
 */
public class NetworkUtil {
    /**
     * 判断手机网络是否可用
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable( Activity activity) {
        ConnectivityManager mgr = (ConnectivityManager) activity.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void toast(final Context context) {
        new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("亲！您的网络连接未打开哦")
                .setPositiveButton("前往打开",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                context.startActivity(intent);
                            }
                        }).setNegativeButton("取消", null).create().show();
    }
}
