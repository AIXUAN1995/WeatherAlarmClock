package com.ax.vertu.weatheralarmclock.adapter;

/**
 * Created by VERTU on 2017/1/21.
 */
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ax.vertu.weatheralarmclock.R;

public class SearchCityAdapter extends ArrayAdapter<String>
{


    public String[] sections;

    public ArrayList<String> mObject;

    public HashMap<String, Integer> alphaIndexer;

    private View view = null;

    private TextView tView = null;

    private ArrayList<String> arrayList = new ArrayList<String>();

    private String[] english = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public SearchCityAdapter(Context context, int textViewResourceId,
                     ArrayList<String> objects)
    {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        initArrayList();
        mObject = objects;
    }

    @Override
    /**
     * 添加item时判断，如果读取到的数据可以在arrayList中找到（即大写的单独字母），则添加为标题，否则是内容
     *
     */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub

        if (arrayList.contains(getItem(position)))
        {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_tag, null);
        }
        else
        {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, null);
        }

        tView = (TextView) view
                .findViewById(R.id.group_list_item_text);
        tView.setText(getItem(position));

        return view;
    }

    /**
     * 根据字母导航所点击的字母，锁定名单中对应标题的位置
     * @param str  所点击的字母
     * @return     返回字母在数据源中的位置。
     */
    public int checkSection(String str)
    {

        for (int i = 0; i < mObject.size(); i++)
        {
            if (mObject.get(i).equals(str))
                return i;
        }
        return -1;
    }

    /**
     * 讲26字母添加进list中，方便getView判断
     */
    public void initArrayList()
    {
        for (int i = 0; i < english.length; i++)
            arrayList.add(english[i]);
    }

}


