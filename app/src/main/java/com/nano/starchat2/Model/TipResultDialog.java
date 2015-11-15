package com.nano.starchat2.Model;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.nano.starchat2.Activity.R;

/**
 * Created by Administrator on 2015/6/27.
 */
public class TipResultDialog extends Dialog {
    private Context m_context;
    private Window window = null;
    private TextView tip;

    public TipResultDialog(Context context)
    {
        super(context);
        m_context = context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tipresult);
    }

    public void showDialog(int x, int y, String str){
        windowDeploy(x, y);

        ///设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);

        tip = (TextView)findViewById(R.id.tipresultdlg_tip);
        tip.setText(str);

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
