package com.nano.starchat2.AsynTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.nano.starchat2.Activity.MainActivity;
import com.nano.starchat2.Activity.R;
import com.nano.starchat2.database.BubblePlayer;
import com.nano.starchat2.MainApplication;
import com.nano.starchat2.Model.CircleImageView;
import com.nano.starchat2.Utils.ConstantsUtil;
import com.nano.starchat2.Utils.DistanceUtil;
import com.nano.starchat2.Utils.JsonBean.Bean_User;
import com.nano.starchat2.Utils.JsonBean.Bean_UserList;
import com.nano.starchat2.database.DBManager;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/1.
 */
public class DrawBubbleTask extends AsyncTask<Integer, BubblePlayer, String>
{
    private Activity mactivity;
    private double mLeftUpLat, mLeftUpLon, mRightDownLat, mRightDownLon;
    //获取区域内用户信息路径
    private String baseUrl = ConstantsUtil.SERVER_IP + ":" + ConstantsUtil.SERVER_PORT + ConstantsUtil.URL_GET_USERS;
    private List<Bean_User> userlist = null;
    private ArrayList<BubblePlayer> bubblePlayers = null;

    public DrawBubbleTask(MainActivity activity,double LeftUpLat, double LeftUpLon, double RightDownLat, double RightDownLon) {
        this.mactivity = activity;
        mLeftUpLat = LeftUpLat;
        mLeftUpLon = LeftUpLon;
        mRightDownLat = RightDownLat;
        mRightDownLon = RightDownLon;
    }

    @Override
    protected String doInBackground(Integer... params) {
        //先看是否登录成功
        SharedPreferences preferences = ((Activity)mactivity).getSharedPreferences(
                ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        String tempUserid = preferences.getString("userid", "");

        String url = baseUrl + "?"
                + "userId="+tempUserid
                + "&latitude=" + new String(Double.toString(0.5*(mLeftUpLat+mRightDownLat)))
                + "&longitude=" + new String(Double.toString(0.5*(mLeftUpLon+mRightDownLon)))
                + "&limit=10"
                + "&distance=" + (DistanceUtil.GetDistance(mLeftUpLat, mLeftUpLon, mRightDownLat, mRightDownLon))/1000;
        //生成一个请求对象
        HttpGet httpGet = new HttpGet(url);
        //生成一个http客户端对象
        HttpClient httpClient = new DefaultHttpClient();
        //使用http客户端发送请求对象
        InputStream inputStream = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() == 200)
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
                /*
                String result = EntityUtils.toString(httpResp.getEntity(),"UTF-8");
                */
                Gson gson = new Gson();
                Bean_UserList AllResult = gson.fromJson(Result, Bean_UserList.class);
                userlist = AllResult.getUserInfo();
            }
            else
            {
                System.out.print("获取服务器数据失败");
            }
        }catch (Exception e){
            e.printStackTrace();;
        }
        finally {
            try{
                inputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if (null == userlist) {
            return null;
        }

        //1.得到所有小球的坐标
        List<BubblePoint> tPointList = new LinkedList<BubblePoint>();

        for(Bean_User user:userlist) {
            LatLng latLng = new LatLng( Double.valueOf(user.getLatitude()),
                    Double.valueOf(user.getLongitude()));
            Point p = (((MainActivity)mactivity).getmBaiduMap()).getProjection().toScreenLocation(latLng);

            BubblePoint  tPoint = new BubblePoint(Integer.valueOf(user.getUser_id()), p.x, p.y);
            tPointList.add(tPoint);
        }

        //如果userid不为空,我们要把本地user加入考虑
        if (!tempUserid.equals(""))
        {
            //boolean IsHaveLocalUser = false;

            //先从userlist里面找有没有本地的用户，看看后台有没有疏漏
            for (Bean_User user:userlist)
            {
                //如果有
                if (user.getUser_id().equals(tempUserid))
                {
                    //删除掉包含本地用户　的点
                    userlist.remove(user);
                    /*user.setLatitude( String.valueOf( ((MainActivity)mactivity).m_dLatitude));
                    user.setLongitude( String.valueOf( ((MainActivity)mactivity).m_dlongitude));
                    IsHaveLocalUser = true;*/
                    break;
                }
            }

            //如果没有的画，就添加一个
            /*if (!IsHaveLocalUser)
            {
                LatLng tmplatLng = new LatLng( ((MainActivity)mactivity).m_dLatitude,
                        ((MainActivity)mactivity).m_dlongitude);
                Point p = (((MainActivity)mactivity).getmBaiduMap()).getProjection().toScreenLocation(tmplatLng);

                BubblePoint  tPoint = new BubblePoint(Integer.valueOf(tempUserid), p.x, p.y);
                tPointList.add(tPoint);
            }*/
        }

        //得到本地的小球坐标
        double nMidLat = ((MainActivity)mactivity).m_dLatitude; //(mLeftUpLat + mRightDownLat)/2;
        double nMidLon = ((MainActivity)mactivity).m_dlongitude; //(mLeftUpLon + mRightDownLon)/2;

        LatLng latLngCenter = new LatLng( nMidLat, nMidLon);
        Point pCenter = (((MainActivity)mactivity).getmBaiduMap()).getProjection().
                toScreenLocation(latLngCenter);


        //2.判断小球之间是否存在重叠
        boolean IsCDFlag = false;
        if (tPointList.size() > 1)
        {
            for (int i = 0; i < tPointList.size() - 1; i++) {
                //取出一个球做为基本球
                BubblePoint tPointBase = tPointList.get(i);

                for (int j = i + 1; j < tPointList.size(); j++)
                {
                    BubblePoint tPoint = tPointList.get(j);

                    //计算两球之间的距离
                    if (  (Math.pow(tPointBase.nX - tPoint.nX, 2) +  Math.pow(tPointBase.nY - tPoint.nY,2))
                            < Math.pow(ConstantsUtil.BUBBLEDIAMETER, 2) ) {
                        //只要有一个交叠，那么这个范围内就有交叠，我们就需要特殊处理一下
                        IsCDFlag = true;
                        break;
                    }
                }

                if (IsCDFlag)
                {
                    break;
                }
            }

            //上面是判断从服务器上下载下来的点之间是否存在交叠，下面是判断本地点与服务器下载点之间的交叠　
            for (int i = 0; i < tPointList.size() - 1; i++)
            {
                //取出一个球做为基本球　
                BubblePoint tPoint = tPointList.get(i);

                //计算两球之间的距离
                if (  (Math.pow(pCenter.x - tPoint.nX, 2) +  Math.pow(pCenter.y - tPoint.nY,2))
                        < Math.pow(ConstantsUtil.BUBBLEDIAMETER, 2) ) {
                    //只要有一个交叠，那么这个范围内就有交叠，我们就需要特殊处理一下
                    IsCDFlag = true;
                    break;
                }
            }

        }
        else {
            IsCDFlag = false;   //  如果一个球都没有得到，那么肯定没有交叠
        }

        //如果有交叠
        if (IsCDFlag)
        {
            //3.改变屏幕上的小球坐标
            //只要有一个交叠，那么这个范围内就有交叠，我们就需要特殊处理一下
            int Num = 0;

            //里面全都是不交叠的状态了
            while (Num < (tPointList.size() + (tPointList.size())*(tPointList.size() - 1)/2))
            {
                Num = 0;
                for (int i = 0; i < tPointList.size(); i++) {
                    //取出一个球做为基本球
                    BubblePoint tPoint = tPointList.get(i);

                    //计算两球之间的距离
                    if (  (Math.pow(pCenter.x - tPoint.nX, 2) +  Math.pow(pCenter.y - tPoint.nY,2))
                            < Math.pow(ConstantsUtil.BUBBLEDIAMETER, 2) )
                    {
                        //只要有一个交叠，那么这个范围内就有交叠，我们就需要特殊处理一下
                        tPoint = ChangePoint(new BubblePoint(Integer.valueOf(tempUserid), pCenter.x, pCenter.y), tPoint);
                        int test = 0;
                    }
                    else
                    {
                        Num++;
                    }
                }

                for (int i = 0; i < tPointList.size() - 1; i++) {
                    //取出一个球做为基本球
                    BubblePoint tPointBase = tPointList.get(i);

                    for (int j = i + 1; j < tPointList.size(); j++)
                    {
                        BubblePoint tPoint = tPointList.get(j);

                        //计算两球之间的距离
                        double x1 = ( Math.pow(tPointBase.nX - tPoint.nX, 2) +  Math.pow(tPointBase.nY - tPoint.nY,2) );
                        double x2 = Math.pow(ConstantsUtil.BUBBLEDIAMETER, 2);
                        if (  (Math.pow(tPointBase.nX - tPoint.nX, 2) +  Math.pow(tPointBase.nY - tPoint.nY,2))
                                < Math.pow(ConstantsUtil.BUBBLEDIAMETER, 2) )
                        {
                            //只要有一个交叠，那么这个范围内就有交叠，我们就需要特殊处理一下
                            double distance1 = Math.pow((tPointBase.nX - pCenter.x), 2) + Math.pow((tPointBase.nY - pCenter.y), 2);
                            double distance2 = Math.pow((tPoint.nX - pCenter.x), 2) + Math.pow((tPoint.nY - pCenter.y), 2);
                            if (distance1 <= distance2)
                            {
                                tPoint = ChangePoint(tPointBase, tPoint);
                            }
                            else
                            {
                                tPointBase = ChangePoint(tPoint, tPointBase);
                            }
                        }
                        else
                        {
                            Num++;
                        }
                    }
                }
            }

            //4.将屏幕坐标转化为经纬度，同时改变BubblePlayer的经纬度
            for(BubblePoint iPoint:tPointList)
            {
                for (Bean_User user:userlist)
                {
                    if (iPoint.id == Integer.valueOf(user.getUser_id()))
                    {
                        LatLng tmpLatLng = new LatLng(Double.valueOf(user.getLatitude()), Double.valueOf(user.getLongitude()));
                        Point p = ((MainActivity) mactivity).getmBaiduMap().getProjection().toScreenLocation(tmpLatLng);
                        p.x = iPoint.nX;
                        p.y = iPoint.nY;
                        tmpLatLng = ((MainActivity) mactivity).getmBaiduMap().getProjection().fromScreenLocation(p);

                        //已经更新了经纬度了
                        user.setLatitude(String.valueOf(tmpLatLng.latitude));
                        user.setLongitude(String.valueOf(tmpLatLng.longitude));
                    }
                }
            }
        }

        //完事之后我们要把本地Userid那个排除掉
        for(Bean_User user:userlist) {
                BubblePlayer player = new BubblePlayer( user.getUser_id() != null ? Integer.valueOf(user.getUser_id()):0,
                        user.getUser_name() != null ? user.getUser_name() : "",
                        user.getLatitude() != null ? Double.valueOf(user.getLatitude()) : 0,
                        user.getLongitude() != null ? Double.valueOf(user.getLongitude()) : 0,
                        user.getHead_img() != null ? user.getHead_img() : "",
                        user.getSex() != null ? user.getSex():""
                );
                publishProgress(player);
        }

        return null;
    }

    //在异步加载之前先把本地数据库中的数据先画出来，使用户感觉马上就画了出来
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        DBManager tempDBManager = ((MainApplication)(mactivity.getApplication())).mdbManager;
        ArrayList<BubblePlayer> bubblePlayerList = new ArrayList<BubblePlayer>();

        //从数据库中查找泡泡
        bubblePlayerList = tempDBManager.queryRectBubbles(mLeftUpLat, mLeftUpLon, mRightDownLat, mRightDownLon);

        for( BubblePlayer i:bubblePlayerList) {
            //如果这个泡泡已经画了，我们就不要画第二遍了
            if (  ((MainActivity)mactivity).getDrawedBubbleMap ().containsKey(i.getId())) {
                System.out.println("已经有id为"+i.getId()+"的泡泡了，我们现就不画了");
            }
            else {
                //将BubblePlayer的坐标提取出来
                LatLng latlong = new LatLng(i.getLatitude(), i.getLontitude());
                MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder().layoutMode(MapViewLayoutParams.ELayoutMode.mapMode).position(latlong);
                MapViewLayoutParams mapViewLayoutPara = builder.build();
                mapViewLayoutPara.height = ConstantsUtil.BUBBLEDIAMETER;  //泡泡的半径
                mapViewLayoutPara.width = ConstantsUtil.BUBBLEDIAMETER;


                ImageView iv = new CircleImageView(mactivity);

                //fortest1
                //i.setHeadImg("http://m1.img.srcdd.com/farm4/d/2012/0817/13/47463D632143F251DCE2B6E44BA158CD_B500_900_500_334.JPEG");

                //如果头像不为空才行
                if (!i.getHeadImg().equals("")) {
                    try {
                        if (i.getSex() == "2") {
                            Picasso.with(mactivity).load(i.getHeadImg())
                                    .placeholder(R.drawable.defaultwememhead)
                                    .error(R.drawable.defaultwememhead)
                                    .into(iv);
                        }
                        else
                        {
                            Picasso.with(mactivity).load(i.getHeadImg())
                                    .placeholder(R.drawable.defaultmanhead)
                                    .error(R.drawable.defaultmanhead)
                                    .into(iv);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    if (i.getSex() == "2") {
                        iv.setImageResource(R.drawable.defaultwememhead);
                    }
                    else
                    {
                        iv.setImageResource(R.drawable.defaultmanhead);
                    }
                }

                /*Animation anim = AnimationUtils.loadAnimation(mactivity, R.anim.breath_up);
                iv.setAnimation(anim);*/

                //将Ｖiew放入百度地图ＶiewGroup
                ((MainActivity) mactivity).getmMapView().addView(iv, mapViewLayoutPara);

                //将其加入 已绘画泡泡 Map
                ((MainActivity) mactivity).getDrawedBubbleMap().put(i.getId(), i);
                //将Ｖiew加入Map中保存起来
                ((MainActivity) mactivity).getDrawedViewMap().put(i.getId(), iv);
            }
        }

         //刷新百度地图
        ((MainActivity) mactivity).getmMapView().refreshDrawableState();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        DBManager tempDBManager = ((MainApplication)(mactivity.getApplication())).mdbManager;
        ArrayList<BubblePlayer> bubblePlayerList = new ArrayList<BubblePlayer>();
        bubblePlayerList = tempDBManager.queryRectBubbles(mLeftUpLat, mLeftUpLon, mRightDownLat, mRightDownLon);

        for( BubblePlayer i:bubblePlayerList) {
            //如果这个泡泡已经画了，我们就不要画第二遍了
            if (  ((MainActivity)mactivity).getDrawedBubbleMap ().containsKey(i.getId())) {
                System.out.println("已经有id为"+i.getId()+"的泡泡了，我们现在把之前的去掉，重新画一下他");
                ImageView iv1= ((MainActivity) mactivity).getDrawedViewMap().get(i.getId());
                iv1.clearAnimation();

                ((MainActivity) mactivity).getDrawedViewMap().remove(iv1);
                ((MainActivity) mactivity).getmMapView().removeView(iv1);

                //刷新地图
                ((MainActivity) mactivity).getmMapView().refreshDrawableState();

                ((MainApplication)(mactivity.getApplication())).playerNum1++;
                System.out.print("**********----------------------------------------**********"+((MainApplication)(mactivity.getApplication())).playerNum1);
            }

            //将BubblePlayer的坐标提取出来
            LatLng latlong = new LatLng(i.getLatitude(), i.getLontitude());
            MapViewLayoutParams.Builder builder = new MapViewLayoutParams.Builder().layoutMode(MapViewLayoutParams.ELayoutMode.mapMode).position(latlong);
            MapViewLayoutParams mapViewLayoutPara = builder.build();

            mapViewLayoutPara.height = 100;
            mapViewLayoutPara.width = 100;
            ImageView iv = new CircleImageView(mactivity);

            //fortest1
            //i.setHeadImg("http://m1.img.srcdd.com/farm4/d/2012/0817/13/47463D632143F251DCE2B6E44BA158CD_B500_900_500_334.JPEG");

            //如果头像不为空才行
            if (!i.getHeadImg().equals("")) {
                try {
                    if (i.getSex() == "2") {
                        Picasso.with(mactivity).load(i.getHeadImg())
                                .placeholder(R.drawable.defaultwememhead)
                                .error(R.drawable.defaultwememhead)
                                .into(iv);
                    }
                    else
                    {
                        Picasso.with(mactivity).load(i.getHeadImg())
                                .placeholder(R.drawable.defaultmanhead)
                                .error(R.drawable.defaultmanhead)
                                .into(iv);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else
            {
                if (i.getSex() == "2") {
                    iv.setImageResource(R.drawable.defaultwememhead);
                }
                else
                {
                    iv.setImageResource(R.drawable.defaultmanhead);
                }
            }

            //将Ｖiew放入百度地图ＶiewGroup
            ((MainActivity) mactivity).getmMapView().addView(iv, mapViewLayoutPara);
            ((MainApplication)(mactivity.getApplication())).playerNum++;
            System.out.print("*********+++++++++++++++++++++++++++++++++++++**********"+((MainApplication)(mactivity.getApplication())).playerNum);

            //将其加入 已绘画泡泡 Map
            ((MainActivity) mactivity).getDrawedBubbleMap().put(i.getId(), i);
            //将Ｖiew加入Map中保存起来
            ((MainActivity) mactivity).getDrawedViewMap().put(i.getId(), iv);

            ((MainActivity) mactivity).getmMapView().refreshDrawableState();
        }
        System.out.print("---postExecute-----------当前已经画了"+((MainActivity) mactivity).getDrawedBubbleMap().size()+ "个泡泡了！-----------------");

        //刷新百度地图
        System.out.print("((((((((((((((((((((((((((MapView当前已存在"+((MainActivity) mactivity).getmMapView().getChildCount()+ "子Ｖiew！-----------------");

    }

    @Override
    protected void onProgressUpdate(BubblePlayer... values) {
        super.onProgressUpdate(values);

        DBManager tempDBManager = ((MainApplication)(mactivity.getApplication())).mdbManager;
        tempDBManager.add(values[0]);
    }

    protected BubblePoint ChangePoint(BubblePoint innerPoint, BubblePoint outPoint)
    {
        //如果
        if (innerPoint.nX == outPoint.nX)
        {
            //如果inPoint 与 outPoint 重合
            if (outPoint.nY == innerPoint.nY) {
                outPoint.nY = innerPoint.nY + ConstantsUtil.BUBBLE_DISTANCE;
                int xyztest = 0;
            }
            else {
                outPoint.nY = innerPoint.nY + (outPoint.nY - innerPoint.nY)/Math.abs(outPoint.nY - innerPoint.nY)
                        *ConstantsUtil.BUBBLE_DISTANCE;
            }
        }
        else
        {
            double k = (outPoint.nY - innerPoint.nY)/(outPoint.nX - innerPoint.nX);
            double k1= (outPoint.nX - innerPoint.nX)* ConstantsUtil.BUBBLE_DISTANCE/Math.pow( k*k + 1, 0.5);
            double k2= (outPoint.nY - innerPoint.nY)*ConstantsUtil.BUBBLE_DISTANCE/Math.pow( 1/(k*k) + 1, 0.5);
            double j1 = Math.abs(outPoint.nX - innerPoint.nX);
            double j2 = Math.abs(outPoint.nY - innerPoint.nY);
            outPoint.nX = innerPoint.nX + (int)(k1/j1);
            outPoint.nY = innerPoint.nY + (int)(k2/j2);
        }

        return  outPoint;
    }
    class BubblePoint{
        int id;
        int nX;
        int nY;

        BubblePoint(int id, int x, int y) {
            this.id = id;
            this.nX = x;
            this.nY = y;
        }
    }
}
