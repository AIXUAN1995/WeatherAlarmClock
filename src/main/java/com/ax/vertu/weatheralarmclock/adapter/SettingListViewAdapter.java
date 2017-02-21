package com.ax.vertu.weatheralarmclock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ax.vertu.weatheralarmclock.R;

import java.util.List;
import java.util.Map;

/**
 * Created by VERTU on 2017/1/26.
 */
public class SettingListViewAdapter extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater mInflater;
    public SettingListViewAdapter(Context context, List<Map<String, Object>> data){
        mInflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_setting, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setBackgroundResource((Integer) data.get(position).get("icon"));
        viewHolder.name.setText((String) data.get(position).get("name"));
        return convertView;
    }

    class ViewHolder{
        public ImageView icon;
        public TextView name;
    }
}
