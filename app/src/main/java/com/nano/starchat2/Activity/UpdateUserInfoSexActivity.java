package com.nano.starchat2.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.adapter.UpdateSexAdapter;

/**
 * Created by Administrator on 2015/6/20.
 */
public class UpdateUserInfoSexActivity extends Activity implements View.OnClickListener{

    private ListView updateSexListView;
    //private EditText editText;
    private UpdateSexAdapter updateSexAdapter;
    //保存
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuserinfo_sex);

        updateSexListView = (ListView)findViewById(R.id.updateuserinfo_sex_ListView);

        updateSexAdapter = new UpdateSexAdapter(this);
        updateSexListView.setAdapter(updateSexAdapter);

        updateSexListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //男
                if (0 == position)
                {
                    updateSexAdapter.setSelect(ConstantsUtil.INT_SEX_MALE);
                }
                //女
                if (1 == position) {
                    updateSexAdapter.setSelect(ConstantsUtil.INT_SEX_FEMALE);
                }

                updateSexAdapter.notifyDataSetChanged();

                //得到当前 输入框中得字符
                String currentText = ConstantsUtil.getSexCh(String.valueOf(position + 1));

                //得到当前界面用户信息，如果一致就不用提交，如果不一致，就改当前界面用户信息
                SharedPreferences preferences = UpdateUserInfoSexActivity.this.getSharedPreferences(
                        ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
                //设置性别
                String sex = preferences.getString("local_sex", "");

                //如果不一致， 就提交改变一次
                if (!currentText.equals(sex))
                {
                    SharedPreferences.Editor editor = preferences.edit();

                    //得到英文性别
                    editor.putString("local_sex", currentText);
                    editor.commit();
                }
            }
        });

        //editText = (EditText)findViewById(R.id.edittext_update_userinfo_sex);
        //btn =(Button)findViewById(R.id.button_update_userinfo_sex_save);
        //btn.setOnClickListener(this);

        TextView textView = (TextView)findViewById(R.id.updateuserinfo_sex_title_back);
        textView.setOnClickListener(this);
    }

    //点击按钮 事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            /*case R.id.button_update_userinfo_sex_save:
            {
                //得到当前 输入框中得字符
                String currentText = editText.getText().toString();

                //得到当前界面用户信息，如果一致就不用提交，如果不一致，就改当前界面用户信息
                SharedPreferences preferences = UpdateUserInfoSexActivity.this.getSharedPreferences(
                        ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
                //设置性别
                String sex = preferences.getString("local_sex", "");

                //如果不一致， 就提交改变一次
                if (!currentText.equals(sex))
                {
                    SharedPreferences.Editor editor = preferences.edit();

                    //得到英文性别
                    editor.putString("local_sex", currentText);
                    editor.commit();
                }

                Intent intent = new Intent(UpdateUserInfoSexActivity.this, UserInfoActivity.class);
                UpdateUserInfoSexActivity.this.startActivity(intent);
                (UpdateUserInfoSexActivity.this).finish();
            }
            break;*/
            //点击返回
            case R.id.updateuserinfo_sex_title_back:
            {
                finish();
            }
            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //先看是否登录成功
        SharedPreferences preferences = this.getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);

        //设置性别
        String sex = preferences.getString("local_sex", "");

        updateSexAdapter.setSelect(Integer.valueOf(ConstantsUtil.getSexNum(sex)));
        updateSexAdapter.notifyDataSetChanged();

        //editText.setText(sex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
