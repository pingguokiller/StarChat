package com.nano.starchat2.Utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConstantsUtil {
    //服务器IP
    public static final String SERVER_IP = "http://182.92.242.44";

    //聊天IP
    public static final String WEBSOCKET_IP = "ws://182.92.242.44";

    //服务器端口
    public static final String SERVER_PORT = "9000";

	public static final int REGISTER_FAIL = 0;//注册失败
	public static final String ACTION = "com.nano.starchat2.message";//消息广播action
	public static final String MSGKEY = "message";//消息的key
	public static final String IP_PORT = "ipPort";//保存ip、port的xml文件名
	public static final String SAVE_USER = "saveUser";//保存用户信息的xml文件名
	public static final String BACKKEY_ACTION="com.way.backKey";//返回键发送广播的action
	public static final int NOTIFY_ID = 0x911;//通知ID
	public static final String DBNAME = "qq.db";//数据库名称

    //头像默认路径
    public static final String MYHEADDIR = "/data/data/com.nano.starchat2/cache/myhead.png";

    //头像上传路径
    public static final String URL_UPLOAD_HEADIMG = "/user/uploadHeadImg";
    //校验手机号路径
    public static final String URL_AUTH_MOBILE = "/register/mobile/";
    //更新个人信息路径
    public static final String URL_UPDATE_USERINFO= "/user/upUserInfo/";
    //获取区域内用户信息路径
    public static final String URL_GET_USERS= "/chat/users/";
    //用户登陆路径
    public static final String URL_LOGIN= "/login/index/";
    //用户注册路径
    public static final String URL_REGISTER= "/register/index/";

    /* 头像文件 */
    public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    /* 请求识别码 */
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    public static int HeadImag_Output_X = 480;
    public static int HeadImag_Output_Y = 480;

    public static final String ERRORCODE_OK = "0";
    public static final String ERRORCODE_INPUTMOBILE = "1002";
    public static final String ERRORCODE_INVALIDMOBILE = "1003";
    public static final String ERRORCODE_EXISTMOBILE = "1004";
    public static final String ERRORCODE_INPUTCODE = "1005";
    public static final String ERRORCODE_ERRORCODE = "1006";
    public static final String ERRORCODE_INPUTUSERNAME = "1007";
    public static final String ERRORCODE_INPUTPASSWORD = "1008";

    public static final String LOGIN_ERRORCODE_FAILURE = "1";

    public static final String PUSHTYPE_CHATMSG = "chatMsg";
    public static final String PUSHTYPE_LOCINFO = "location";
    public static final String PUSHTYPE_LOGIN= "login";

    public static final String MSGTYPE_CHATMSG = "text";

    public static final String SHAREDPREFERENCES_NAME = "first_pref";

    //默认泡泡的直径
    public static final int  BUBBLEDIAMETER = 100;

    //泡泡之间最好有这样的距离
    public static final int  BUBBLE_DISTANCE = 120;

    //聊天汉字一个字占用的屏幕宽度
    public static final int  TEXTWIDTHUNIT = 70;


    //关于男女的常量定义
    public static final int INT_SEX_UNKOWN = 0;
    public static final int INT_SEX_MALE = 1;
    public static final int INT_SEX_FEMALE = 2;

    public static final String STRING_SEX_UNKOWN = "0";
    public static final String STRING_SEX_MALE = "1";
    public static final String STRING_SEX_FEMALE = "2";


    public static String getSexCh(String sex) {
        if (sex.equals(STRING_SEX_MALE))
        {
            sex = "男";
        }
        else if (sex.equals(STRING_SEX_FEMALE))
        {
            sex = "女";
        }
        else
        {
            sex = "";
        }
        return sex;
    }

    public static String getSexNum(String sex) {
        if (sex.equals("男"))
        {
            sex = STRING_SEX_MALE;
        }
        else if (sex.equals("女"))
        {
            sex = STRING_SEX_FEMALE;
        }
        else
        {
            sex = STRING_SEX_UNKOWN;
        }
        return sex;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }

    //保存图片
    public static void saveMyBitmap(String strdir, Bitmap bitmap) throws IOException {
        File f = new File(strdir);
        boolean bret = f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bret = bitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
