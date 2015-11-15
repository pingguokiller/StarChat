package com.nano.starchat2.Model;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nano.starchat2.Activity.R;
import com.nano.starchat2.Activity.UserInfoActivity;

/**
 * Created by Administrator on 2015/6/27.
 */
public class HeadImageMngDialog extends Dialog {
    private Context m_context;
    private Window window = null;
    private TextView camera;
    private TextView dicm;
    private TextView cancel;
    public HeadImageMngDialog(Context context)
    {
        super(context);
        m_context = context;
    }

    public void showDialog(int layoutResID, int x, int y){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutResID);

        windowDeploy(x, y);

        ///设置触摸对话框意外的地方取消对话框
        //setCanceledOnTouchOutside(true);

        camera = (TextView)findViewById(R.id.headimgdlg_camera);
        dicm = (TextView)findViewById(R.id.headimgdlg_dicm);
        cancel = (TextView)findViewById(R.id.headimgdlg_cancel);

        camera.setOnClickListener((UserInfoActivity)m_context);
        dicm.setOnClickListener((UserInfoActivity)m_context);
        cancel.setOnClickListener((UserInfoActivity)m_context);
        show();
    }

    //设置窗口显示
    public void windowDeploy(int x, int y){
        window = getWindow(); //得到对话框
        window.setWindowAnimations(R.style.HeadImgdialogWindowAnim); //设置窗口弹出动画
        window.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
        WindowManager.LayoutParams wl = window.getAttributes();
        //根据x，y坐标设置窗口需要显示的位置
        wl.x = x; //x小于0左移，大于0右移
        wl.y = y; //y小于0上移，大于0下移
        //wl.alpha = 0.6f; //设置透明度
        wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(wl);
    }
}
