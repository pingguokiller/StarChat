package com.nano.starchat2.AsynTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.nano.starchat2.Utils.ConstantsUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/11.
 */
public class ChangeUserInfoTask extends AsyncTask<Integer, Integer, String> {
    private String userId;
    private String userName;
    private String realName;
    //private String nickName;
    private String identityCart;
    private String youLines;
    private String sex;
    private String birthday;
    private String nation;
    private String region;
    private String city;
    //更新个人信息路径
    private String baseUrl = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_UPDATE_USERINFO;

    private Context context;
    //Bean_RegResult RegResult;

    public ChangeUserInfoTask(Context context, String inSex) {
        super();
        userId = "";
        userName = "";
        realName = "";
        //nickName = "";
        identityCart = "";
        youLines = "";
        sex = "";
        birthday = "";
        nation = "";
        region = "";
        city = "";

        //this.nick_name = nickName;
        sex = inSex;

        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        /*
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
        }*/
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... params) {

        SharedPreferences preferences = context.getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);

        String sexNum = ConstantsUtil.getSexNum(sex);

        userId = preferences.getString("userid","");
        userName = preferences.getString("username","");

        if (userId.equals("") || userName.equals(""))
        {
            return null;
        }

        String url = baseUrl;  // + "?"
               /* + "userId=" + userId
                + "&userName=" + userName
                + "&realName=" + realName
                + "&identityCart=" + identityCart
                + "&sex=" + sexNum
                + "&birthday=" + birthday
                + "&nation=" + nation
                + "&region=" + region
                + "&city=" + city
                ;*/

        //生成一个请求对象
        // 第一步，创建HttpPost对象
        HttpPost httpPost = new HttpPost(url);

        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("userId", userId));
        postParams.add(new BasicNameValuePair("userName", userName));
        postParams.add(new BasicNameValuePair("realName", realName));
        postParams.add(new BasicNameValuePair("identityCart", identityCart));
        postParams.add(new BasicNameValuePair("sex", sexNum));
        postParams.add(new BasicNameValuePair("birthday", birthday));
        postParams.add(new BasicNameValuePair("nation", nation));
        postParams.add(new BasicNameValuePair("region", region));
        postParams.add(new BasicNameValuePair("city", city));

        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            //System.out.println(httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                String result = EntityUtils.toString(httpResponse.getEntity());
                System.out.println("result:" + result);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
