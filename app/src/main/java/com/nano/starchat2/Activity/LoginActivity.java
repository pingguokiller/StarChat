package com.nano.starchat2.Activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nano.starchat2.AsynTask.AuthTask;
import com.nano.starchat2.AsynTask.LoginTask;
import com.nano.starchat2.AsynTask.RegisterTask;
import com.nano.starchat2.Utils.DialogFactory;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
    private EditText txUserName;
    private EditText txPassword;
    private EditText txPhone;
    private EditText txAuthcode;
    private Button btnAuth;
    private Button btnLogin;
    private Button btnRegister;
    private TextView tvPhoneRet;
    private TextView tvCodeRet;
    private TextView tvRegRet;

    //是否校验成功
    private boolean IsAuth = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //fortest
        IsAuth = true;

        initView();
        setListener();
    }

    private void initView() {
        btnLogin=(Button)findViewById(R.id.login_btnLogin);
        btnRegister=(Button)findViewById(R.id.login_btnRegister);
        txUserName=(EditText)findViewById(R.id.login_UserName);
        txPassword=(EditText)findViewById(R.id.login_textPasswd);

        txPhone=(EditText)findViewById(R.id.login_phone);
        btnAuth=(Button)findViewById(R.id.login_btnauth);
        txAuthcode=(EditText)findViewById(R.id.login_authcode);

        tvPhoneRet = (TextView)findViewById(R.id.login_phoneResult);
        tvCodeRet = (TextView)findViewById(R.id.login_codeResult);
        tvRegRet = (TextView)findViewById(R.id.login_RegResult);
    }

    private void setListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsAuth)
                {
                    String userName = txUserName.getText().toString();
                    String password = txPassword.getText().toString();
                    String phone = txPhone.getText().toString();
                    String authcode=txAuthcode.getText().toString();

                    RegisterTask registerTask = new RegisterTask(LoginActivity.this, userName, password, phone, authcode);
                    registerTask.execute();
                }
                else
                {
                    DialogFactory.ToastDialog(LoginActivity.this, "QQ注册", "手机尚校验成功！");
                }
            }
        });
        //waitdo
        //这里登录
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsAuth)
                {
                    String userName = txUserName.getText().toString();
                    String password = txPassword.getText().toString();

                    LoginTask loginTask = new LoginTask(LoginActivity.this, userName, password, true);
                    loginTask.execute();
                }
                else
                {
                    DialogFactory.ToastDialog(LoginActivity.this, "QQ注册", "手机尚校验成功！");
                }
            }
        });

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone=txPhone.getText().toString();
                String authcode=txAuthcode.getText().toString();

                AuthTask authTask = new AuthTask(LoginActivity.this, phone, authcode);
                authTask.execute();
            }
        });
    }

    public TextView getTvPhoneRet() {
        return tvPhoneRet;
    }

    public TextView getTvCodeRet() {
        return tvCodeRet;
    }

    public TextView getTvRegRet() {
        return tvRegRet;
    }

    public void setAuth(boolean isAuth) {
        IsAuth = isAuth;
    }
}
