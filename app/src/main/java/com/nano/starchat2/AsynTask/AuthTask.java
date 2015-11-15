package com.nano.starchat2.AsynTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.nano.starchat2.Activity.RegisterCodeActivity;
import com.nano.starchat2.Activity.RegisterIdPasswordActivity;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.DialogFactory;
import com.nano.starchat2.Utils.JsonBean.Bean_CodeResult;

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
public class AuthTask extends AsyncTask<Integer, Integer, String> {
    private String phone;
    private String authCode;
    //校验手机号 请求路径
    private String baseUrl = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_AUTH_MOBILE;
    private Dialog mDialog = null;
    private Context context;
    Bean_CodeResult AllResult;

    public AuthTask(Context context, String phone, String authCode) {
        super();
        this.phone = phone;
        this.authCode = authCode;
        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        if (null != AllResult) {
            if (AllResult.getCode().equals(ConstantsUtil.ERRORCODE_OK)) {
                // 校验成功 跳转到下一步
                Intent intent = new Intent(((RegisterCodeActivity)context), RegisterIdPasswordActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("authCode", authCode);
                ((RegisterCodeActivity)context).startActivity(intent);
            }
            else {
                String tipStr = AllResult.getInfo();
                //多个Activity嵌套时用this.parent否则异常
                ((RegisterCodeActivity)context).tipRetDlg.showDialog(0, 50, tipStr);
            }

        }
        else {
            String tipStr = "服务器未返回校验结果！";
            //多个Activity嵌套时用this.parent否则异常
            ((RegisterCodeActivity)context).tipRetDlg.showDialog(0, 50, tipStr);
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
                + "&authcode=" + authCode;

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
                AllResult = gson.fromJson(Result, Bean_CodeResult.class);

            }
            else
            {
                System.out.print("获取服务器数据失败");

                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                DialogFactory.ToastDialog(context,"QQ注册手机校验", "获取服务器数据失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
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
        mDialog = DialogFactory.creatRequestDialog(context, "正在校验中...");
        mDialog.show();
    }
}
