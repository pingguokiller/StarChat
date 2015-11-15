package com.nano.starchat2.AsynTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.nano.starchat2.Activity.MainActivity;
import com.nano.starchat2.Activity.RegisterIdPasswordActivity;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.DialogFactory;
import com.nano.starchat2.Utils.JsonBean.Bean_RegResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/3/11.
 */
public class RegisterTask extends AsyncTask<Integer, Integer, String> {
    private String userName;
    private String password;
    private String phone;
    private String authcode;
    //用户注册路径
    private String baseUrl = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_REGISTER;

    private Dialog mDialog = null;
    private Context context;
    Bean_RegResult RegResult;

    public RegisterTask(Context context, String userName, String password, String phone, String authcode) {
        super();
        this.userName = userName;
        this.password = password;
        this.phone = phone;
        this.authcode = authcode;
        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        if (null != RegResult) {
            //注册成功，保存一下登录用户名以及密码，然后进入主页面
            if (RegResult.getErrno().equals(ConstantsUtil.ERRORCODE_OK)) {
                //保存用户名 , id, 密码
                SharedPreferences preferences = context.getSharedPreferences(
                        ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                editor.putString("username", userName);
                editor.putString("password", password);
                editor.putString("userid", RegResult.getUserInfo().getUser_id());
                editor.putBoolean("IsLogin", true);
                // 提交修改
                editor.commit();

                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            else
            {
                String tipStr = RegResult.getInfo();
                //多个Activity嵌套时用this.parent否则异常
                ((RegisterIdPasswordActivity)context).tipRetDlg.showDialog(0, 50, tipStr);
            }

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showRequestDialog();
    }

    @Override
    protected String doInBackground(Integer... params) {
        String url = baseUrl + "?"
                + "mobile=" + phone
                + "&authcode=" + authcode
                + "&uname=" + userName
                + "&pass=" + password
                ;
        //生成一个请求对象
        HttpGet httpGet = new HttpGet(url);

        //生成一个http客户端对象
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);//连接时间
        //httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,2000);//数据传输时间

        //使用http客户端发送请求对象
        InputStream inputStream = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                //获取返回胡数据
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String Result = "";
                String line = "";
                while((line = reader.readLine()) != null) {
                    Result += line;
                }
                System.out.print("Get From Net----------------->"+Result);

                Gson gson = new Gson();
                RegResult = gson.fromJson(Result, Bean_RegResult.class);
                int i = 0;
            }
            else
            {
                System.out.print("获取服务器数据失败");

                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                DialogFactory.ToastDialog(context,"QQ注册", "获取服务器数据失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            DialogFactory.ToastDialog(context,"QQ注册", "获取服务器数据失败");
        }
        finally {
            try{
                if (null != inputStream)
                 inputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }



    private void showRequestDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        mDialog = DialogFactory.creatRequestDialog(context, "正在注册中...");
        mDialog.show();
    }
}
