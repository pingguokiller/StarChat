package com.nano.starchat2.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nano.starchat2.AsynTask.LoginTask;
import com.nano.starchat2.Model.TipResultDialog;

public class Login2Activity extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */
    private EditText txUserName;
    private EditText txPassword;
    private Button btnLogin;
    private TextView tvResgister;

    public TextView tvLoginRet;
    public TipResultDialog tipRetDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login2);

        tipRetDlg = new TipResultDialog(Login2Activity.this);

        initView();
        setListener();
    }

    private void initView() {
        btnLogin=(Button)findViewById(R.id.login2_btnLogin);
        tvResgister=(TextView)findViewById(R.id.login2_textview_resgister);

        txUserName=(EditText)findViewById(R.id.login2_userid);
        txPassword=(EditText)findViewById(R.id.login2_password);
    }

    public void onClick(View v) {
        switch(v.getId())
        {
            //登录
            case R.id.login2_btnLogin:
            {
                String userName = txUserName.getText().toString();
                String password = txPassword.getText().toString();

                LoginTask loginTask = new LoginTask(Login2Activity.this, userName, password, true);
                loginTask.execute();
            }
            break;
            case R.id.login2_textview_resgister:        //注册
            {
                // 跳转 到 输入手机号界面
                Intent intent = new Intent(this, RegisterPhoneActivity.class);
                this.startActivity(intent);
            }
            break;
        }
      }

    //设置监听器
    private void setListener() {
        btnLogin.setOnClickListener(this);
        tvResgister.setOnClickListener(this);
    }

}
