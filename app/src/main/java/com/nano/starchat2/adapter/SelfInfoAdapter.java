package com.nano.starchat2.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nano.starchat2.Activity.R;
import com.nano.starchat2.Utils.ConstantsUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/6/20.
 */
public class SelfInfoAdapter extends BaseAdapter {
    Context m_context;
    ArrayList<HashMap<String, Object>> listItem;

    public SelfInfoAdapter(Context context)
    {
        m_context = context;

        listItem = new ArrayList<HashMap<String, Object>>();
        //初始化数据
        initData();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();

        //设置 listitem 内部结构
        if (convertView == null) {
            //头像
            if (0 == position)
            {
                convertView =  LayoutInflater.from(m_context).inflate(
                        R.layout.selfinfo_listview_item0, null);   //头像 加载的是带头像的布局

                holder.title = (TextView)convertView.findViewById(R.id.selfinfo_listitem0_name);
                holder.tip = (ImageView)convertView.findViewById(R.id.selfinfo_listitem0_tip);
                holder.viewContent = (ImageView)convertView.findViewById(R.id.selfinfo_listitem0_viewcontent);
            }
            //昵称与性别
            else if (1 == position || 2 ==position)
            {
                convertView =  LayoutInflater.from(m_context).inflate(
                        R.layout.selfinfo_listview_item, null); //昵称性别等，加载的是不一样的布局

                holder.title = (TextView) convertView.findViewById(R.id.selfinfo_listitem_name);
                holder.tip = (ImageView) convertView.findViewById(R.id.selfinfo_listitem_tip);
                holder.text = (TextView)convertView.findViewById(R.id.selfinfo_listitem_textcontent);
            }
            else
            {
                convertView =  LayoutInflater.from(m_context).inflate(
                        R.layout.selfinfo_listview_item, null);

                holder.title = (TextView) convertView.findViewById(R.id.selfinfo_listitem_name);
                holder.tip = (ImageView) convertView.findViewById(R.id.selfinfo_listitem_tip);
            }
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(listItem.get(position).get("ItemTitle").toString());
        int resId = Integer.valueOf(listItem.get(position).get("ItemTip").toString());
        holder.tip.setImageResource(resId);

        //设置显示资源
        if (0 == position)
        {
            //从本地读取头像，并显示
            Bitmap bmp = BitmapFactory.decodeFile(ConstantsUtil.MYHEADDIR);
            holder.viewContent.setImageBitmap(bmp);
        }
        else if (1 == position || 2 ==position)
        {
            if (1 == position)
            {
                holder.text.setText(listItem.get(position).get("ItemNickName").toString());
            }
            else if (2 == position)
            {
                holder.text.setText(listItem.get(position).get("ItemSex").toString());
            }
        }
        else
        {
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    public final class ViewHolder{
        public TextView  title;
        public TextView  text;
        public ImageView viewContent;
        public ImageView tip;
    }

    public void initData()
    {
        //得到preferences
        SharedPreferences preferences = m_context.getSharedPreferences(ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", "头像");
        map.put("ItemTip", R.drawable.usermenu_tip);
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemTitle", "名称");
        map.put("ItemTip", R.drawable.usermenu_tip);
        String nickname = preferences.getString("local_nickname", "");
        map.put("ItemNickName", nickname);
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemTitle", "性别");
        map.put("ItemTip", R.drawable.usermenu_tip);
        String sex = preferences.getString("local_sex", "");
        map.put("ItemSex", sex);
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemTitle", "个性签名");
        map.put("ItemTip", R.drawable.usermenu_tip);
        listItem.add(map);
    }


    public void setData(int position, String key, Object str)
    {
         listItem.get(position).put(key,str);
    }
}
