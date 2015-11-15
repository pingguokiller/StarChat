package com.nano.starchat2.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nano.starchat2.AsynTask.ChangeUserInfoTask;
import com.nano.starchat2.Model.HeadImageMngDialog;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.JsonBean.Bean_UploadHeadImgResult;
import com.nano.starchat2.adapter.SelfInfoAdapter;
import com.nano.starchat2.netclient.HttpUpload.AsyncHttpClient;
import com.nano.starchat2.netclient.HttpUpload.AsyncHttpResponseHandler;
import com.nano.starchat2.netclient.HttpUpload.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Administrator on 2015/6/20.
 */
public class UserInfoActivity extends Activity implements View.OnClickListener{
    private ListView selfInfoListView;
    SelfInfoAdapter selfInfoAdapter;
    private HeadImageMngDialog headImageMngDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        selfInfoListView = (ListView)findViewById(R.id.SelfInfo_ListView);

        selfInfoAdapter = new SelfInfoAdapter(this);
        selfInfoListView.setAdapter(selfInfoAdapter);

        selfInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //头像管理
                if (0 == position)
                {
                    headImageMngDialog = new HeadImageMngDialog(UserInfoActivity.this);
                    //多个Activity嵌套时用this.parent否则异常
                    headImageMngDialog.showDialog(R.layout.dialog_headimg, 0, 50);
                }
                //如果点击的是第3行　"性别"
                if (2 == position) {
                    Intent intent = new Intent(UserInfoActivity.this, UpdateUserInfoSexActivity.class);
                    UserInfoActivity.this.startActivity(intent);

                }
            }
        });

        TextView textView = (TextView)findViewById(R.id.userinfo_title_back);
        textView.setOnClickListener(this);
    }

    //点击按钮 事件
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.headimgdlg_cancel:
            {
                headImageMngDialog.dismiss();
            }
            break;
            //拍照
            case R.id.headimgdlg_camera:
            {
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (ConstantsUtil.hasSdcard())
                {
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(), ConstantsUtil.IMAGE_FILE_NAME)));
                }
                startActivityForResult(intentFromCapture, ConstantsUtil.CODE_CAMERA_REQUEST);
            }
            break;
            case R.id.headimgdlg_dicm:
            {
                Intent intentFromGallery = new Intent();
                // 设置文件类型
                intentFromGallery.setType("image/*");
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, ConstantsUtil.CODE_GALLERY_REQUEST);
            }
            break;
            case R.id.userinfo_title_back:
            {
                finish();
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
            return;
        }

        switch (requestCode) {
            case ConstantsUtil.CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;

            case ConstantsUtil.CODE_CAMERA_REQUEST:
                if (ConstantsUtil.hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            ConstantsUtil.IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG)
                            .show();
                }
                break;

            case ConstantsUtil.CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }

                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", ConstantsUtil.HeadImag_Output_X);
        intent.putExtra("outputY", ConstantsUtil.HeadImag_Output_Y);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, ConstantsUtil.CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) ;{
            //ImageView headImage  = (ImageView)findViewById(R.id.userinfo_headimg);

            Bitmap photo = extras.getParcelable("data");
            try {
                String strdir = ConstantsUtil.MYHEADDIR;
                ConstantsUtil.saveMyBitmap(strdir, photo);
                selfInfoAdapter.notifyDataSetChanged();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            //设置完头像之后上传头像
            //upload(photo);
            try {
                upLoadByAsyncHttpClient(ConstantsUtil.MYHEADDIR);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * upLoadByAsyncHttpClient:由AsyncHttpClient框架上传
     *
     * @return void
     * @throws FileNotFoundException
     * @throws
     * @since CodingExample　Ver 1.1
     */
    private void upLoadByAsyncHttpClient(String path) throws FileNotFoundException {
        //先看是否登录成功
        SharedPreferences preferences = this.getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        Boolean tempIsBoolen = preferences.getBoolean("IsLogin", false);
        //如果还没有登录成功， 我们要先登录
        if (!tempIsBoolen)
        {
            return;
        }

        String tempUserid = preferences.getString("userid", "");

        RequestParams params = new RequestParams();
        params.put("userId", tempUserid);
        params.put("headImg", new File(path));
        AsyncHttpClient client = new AsyncHttpClient();

        String tempStr = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_UPLOAD_HEADIMG;
        client.post(tempStr, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (statusCode == 200) {
                        String Result = new String(responseBody, "UTF-8");
                        Gson gson = new Gson();
                        Bean_UploadHeadImgResult uploadHeadImgResult = gson.fromJson(Result, Bean_UploadHeadImgResult.class);
                        int test = 0;
                        System.out.println("头像上传成功!");
                    } else {
                        System.out.println("网络访问异常，错误码：" + statusCode);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                int test = 0;
                //Toast.makeText(UserInfoAcitivity.this"网络访问异常，错误码  > " + statusCode, 0).show();
            }
        });
    }

    /*
	 * 上传图片
	 * 使用二进制流方式
	 */
    private void upload(Bitmap bitmap) {
        //先看是否登录成功
        SharedPreferences preferences = this.getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        Boolean tempIsBoolen = preferences.getBoolean("IsLogin", false);
        //如果还没有登录成功， 我们要先登录
        if (!tempIsBoolen)
        {
            return;
        }

        String tempUserid = preferences.getString("userid", "");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            byte[] buffer = out.toByteArray();

            byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
            String photo = new String(encode);

            RequestParams params = new RequestParams();
            params.put("headImg", photo);
            params.put("userId", tempUserid);

            int x = photo.getBytes().length;
            System.out.println("上传头像字节大小-------------" + x);

            //头像上传路径
            String url = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_UPLOAD_HEADIMG;

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        if (statusCode == 200) {
                            String Result = new String(responseBody, "UTF-8");
                            Gson gson = new Gson();
                            Bean_UploadHeadImgResult uploadHeadImgResult = gson.fromJson(Result, Bean_UploadHeadImgResult.class);
                            int test = 0;
                            //Toast.makeText(UserInfoAcitivity.this, "头像上传成功!", 0).show();
                        } else {
                            //Toast.makeText(UserInfoAcitivity.this "网络访问异常，错误码：" + statusCode, 0).show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    int test = 0;
                    //Toast.makeText(UserInfoAcitivity.this"网络访问异常，错误码  > " + statusCode, 0).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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
        //刷新一下listView
        //selfInfoAdapter.notifyDataSetChanged();

        //先看是否登录成功
        SharedPreferences preferences = this.getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);

        /*String nickName = preferences.getString("local_nickname", "");
        selfInfoAdapter.setData(2, "ItemNickName", nickName);*/

        String sex = preferences.getString("local_sex", "");
        selfInfoAdapter.setData(2, "ItemSex", sex);
        selfInfoAdapter.notifyDataSetChanged();

        //如果说发育本地的昵称与　网络上的数据不一致，　我们就需要上传修改
        String NetSex = preferences.getString("sex", "");
        if (!NetSex.equals(sex))
        {
            ChangeUserInfoTask changeUserInfoTask = new ChangeUserInfoTask(this, sex);
            changeUserInfoTask.execute();
        }
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
