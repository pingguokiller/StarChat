package com.nano.starchat2.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterPhoneActivity extends Activity implements View.OnClickListener{
    /** Called when the activity is first created. */

    private EditText etPhone;
    private TextView tvOver;
    private TextView tvBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registerphone);

        initView();
        setListener();
    }

    private void initView() {
        etPhone=(EditText)findViewById(R.id.registerphone_phonenumber);
        tvOver = (TextView)findViewById(R.id.registerphone_title_over);
        tvBack = (TextView)findViewById(R.id.registerphone_title_back);
    }

    public void onClick(View v) {
        switch(v.getId())
        {
            //完成
            case R.id.registerphone_title_over:
            {
                String phone = etPhone.getText().toString();

                // 跳转
                Intent intent = new Intent(this, RegisterCodeActivity.class);
                intent.putExtra("phone", phone);
                this.startActivity(intent);
            }
            break;
            case R.id.registerphone_title_back:        //返回
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
