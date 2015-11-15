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
import com.nano.starchat2.Utils.JsonBean.Bean_LoginResult;

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
public class LoginTask extends AsyncTask<Integer, Integer, String> {
    private String userName;
    private String password;
    private Boolean isFromLoginActivity;
    //用户登陆路径
    private String baseUrl = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_LOGIN;
    private Dialog mDialog = null;
    private Context context;
    Bean_LoginResult LoginResult;

    public LoginTask(Context context, String userName, String password, Boolean IsFromLoginActivity) {
        super();
        this.userName = userName;
        this.password = password;
        this.isFromLoginActivity = IsFromLoginActivity;
        this.context = context;
    }

    public LoginTask(Context context, String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
        this.isFromLoginActivity = false;
        this.context = context;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        if (null != LoginResult) {

            //注册成功，保存一下登录用户名以及密码，然后进入主页面
            if (LoginResult.getErrno().equals(ConstantsUtil.ERRORCODE_OK)) {
                //保存用户名 , id, 密码
                //写是否已经引导过了
                SharedPreferences preferences = context.getSharedPreferences(
                        ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
                SharedPreferences.Editor editor = preferences.edit();
                // 存入数据
                editor.putString("username", userName);
                editor.putString("password", password);
                editor.putString("userid", LoginResult.getUserInfo().getUser_id());  //用户id
                //editor.putString("nickname", LoginResult.getUserInfo().getNickname()); //昵称

                //只存中文性别
                String SexCh = ConstantsUtil.getSexCh(LoginResult.getUserInfo().getSex());
                editor.putString("sex",SexCh); //性别

                editor.putBoolean("IsLogin", true);

                //获取本地界面　用户信息，如果为空或者用户ＩＤ对不上(更换用户)　就更新一下本地界面　用户信息
                String localUserId = preferences.getString("local_userid", "");
                //editor.putString("local_username", userName);
                //editor.putString("password", password);

                //如果true的话需要将　网张上请求的数据　更新给本地界面　
                if (localUserId.equals("") || localUserId.equals(LoginResult.getUserInfo().getUser_id()) )
                {
                    //用户名
                    editor.putString("local_userid", LoginResult.getUserInfo().getUser_id());

                    //昵称
                    //editor.putString("local_nickname", LoginResult.getUserInfo().getNickname());
                    //性别
                    editor.putString("local_sex", SexCh);
                }
                // 提交修改
                editor.commit();

                //无论是否成功，我们都会进入主界面
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
            //如果登录不成功
            else {
                //如果是从登录界面过来的。
                if (isFromLoginActivity) {
                    String tipStr = LoginResult.getInfo();
                    //多个Activity嵌套时用this.parent否则异常
                    ((RegisterIdPasswordActivity)context).tipRetDlg.showDialog(0, 50, tipStr);
                }
                else
                {
                    //只要不是从LoginAcitivity过来的，  就算不成功，我们都会进入主界面
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            }

        }
        else
        {
            //((LoginActivity) context).getTvRegRet().setText("获取服务器数据失败！");
            //只要不是从LoginAcitivity过来的，就算不成功，我们都会进入主界面
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //显示登录信息 让用户等待
        showRequestDialog();
    }

    @Override
    protected String doInBackground(Integer... params) {
        String url = baseUrl + "?"
                + "uname=" + userName
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
                LoginResult = gson.fromJson(Result, Bean_LoginResult.class);

            }
            else
            {
                System.out.print("获取服务器数据失败");

                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                if(null!=inputStream)
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
        mDialog = DialogFactory.creatRequestDialog(context, "正在登录中...");
        mDialog.show();
    }
}
