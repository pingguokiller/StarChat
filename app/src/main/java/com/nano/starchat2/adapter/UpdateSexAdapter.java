package com.nano.starchat2.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
public class UpdateSexAdapter extends BaseAdapter {
    Context m_context;
    ArrayList<HashMap<String, Object>> listItem;

    private int m_select = 0;     //0： 表示什么都不头选， 1 ：男     2：女
    public UpdateSexAdapter(Context context)
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
            convertView =  LayoutInflater.from(m_context).inflate(
                        R.layout.updatesex_listview_item, null);
            holder.title = (TextView)convertView.findViewById(R.id.updatesex_listitem_name);
            holder.select = (ImageView)convertView.findViewById(R.id.updatesex_listitem_select);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(listItem.get(position).get("ItemTitle").toString());
        holder.select.setImageBitmap(null);
        //holder.select.setImageResource(R.drawable.backcolor_white_drawable);
        //当前什么都没有选择
        if (0 == m_select)
        {

        }
        else if (1 == m_select)     //男  ；
        {
            if (0 == position)
            {
                holder.select.setImageResource(R.drawable.right);
            }
        }
        else if (2 == m_select)     //女  ；
        {
            if (1 == position)
            {
                holder.select.setImageResource(R.drawable.right);
            }
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
        public ImageView select;
    }

    public void initData()
    {
        //得到preferences
        SharedPreferences preferences = m_context.getSharedPreferences(ConstantsUtil.SHAREDPREFERENCES_NAME, Context.MODE_MULTI_PROCESS);

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", "男");
        listItem.add(map);

        map = new HashMap<String, Object>();
        map.put("ItemTitle", "女");
        listItem.add(map);
    }


    public void setData(int position, String key, Object str)
    {
        listItem.get(position).put(key,str);
    }

    public void setSelect(int x)
    {
        m_select = x;   // 设置当前选择的是什么
    }
}
