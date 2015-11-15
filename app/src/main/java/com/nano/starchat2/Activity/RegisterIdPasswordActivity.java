package com.nano.starchat2.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nano.starchat2.AsynTask.RegisterTask;
import com.nano.starchat2.Model.TipResultDialog;

public class RegisterIdPasswordActivity extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */
    private EditText txUserName;
    private EditText txPassword;
    private Button btnRegister;
    private TextView tvGoin;

    private String phone;
    private String authcode;

    public TextView tvLoginRet;
    public TipResultDialog tipRetDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registerpassword);

        tipRetDlg = new TipResultDialog(RegisterIdPasswordActivity.this);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        authcode = intent.getStringExtra("authCode");

        initView();
        setListener();
    }

    private void initView() {
        btnRegister=(Button)findViewById(R.id.registerpassword_btnRegister);
        tvGoin=(TextView)findViewById(R.id.registerpassword_textview_go);

        txUserName=(EditText)findViewById(R.id.registerpassword_userid);
        txPassword=(EditText)findViewById(R.id.registerpassword_password);
    }

    public void onClick(View v) {
        switch(v.getId())
        {
            //登录
            case R.id.registerpassword_btnRegister:
            {
                String userName = txUserName.getText().toString();
                String password = txPassword.getText().toString();

                RegisterTask registerTask = new RegisterTask(RegisterIdPasswordActivity.this, userName, password, phone, authcode);
                registerTask.execute();
            }
            break;
            case R.id.registerpassword_textview_go:        //注册
            {
                // 跳转 到 输入手机号界面
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
            }
            break;
        }
      }

    //设置监听器
    private void setListener() {
        btnRegister.setOnClickListener(this);
        tvGoin.setOnClickListener(this);
    }

}
