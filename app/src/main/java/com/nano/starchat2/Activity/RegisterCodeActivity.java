package com.nano.starchat2.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nano.starchat2.AsynTask.AuthTask;
import com.nano.starchat2.Model.TipResultDialog;

public class RegisterCodeActivity extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */

    private EditText etCode;
    private TextView tvOver;
    private TextView tvBack;
    private String phone;

    public  TipResultDialog tipRetDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registercode);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        tipRetDlg = new TipResultDialog(RegisterCodeActivity.this);
        initView();
        setListener();
    }

    private void initView() {
        etCode = (EditText)findViewById(R.id.registercode_code);
        tvOver = (TextView)findViewById(R.id.registercode_title_over);
        tvBack = (TextView)findViewById(R.id.registercode_title_back);
    }

    public void onClick(View v) {
        switch(v.getId())
        {
            //完成
            case R.id.registercode_title_over:
            {
                String authcode = etCode.getText().toString();

                AuthTask authTask = new AuthTask(RegisterCodeActivity.this, phone, authcode);
                authTask.execute();
            }
            break;
            case R.id.registercode_title_back:        //返回
            {
                finish();
            }
            break;
        }
      }

    //设置监听器
    private void setListener() {
        tvOver.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

}
