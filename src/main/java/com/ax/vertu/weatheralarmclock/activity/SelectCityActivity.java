package com.ax.vertu.weatheralarmclock.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.adapter.SearchCityAdapter;
import com.ax.vertu.weatheralarmclock.tools.Cities;
import com.ax.vertu.weatheralarmclock.tools.Sort;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;

public class SelectCityActivity extends AppCompatActivity implements View.OnTouchListener {

    public static final int RESULTCODE = 1;
    private static final String TAG = "CityActivity";
    private char[] Letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private Context mContext;
    private ListView lv_cities;
    private LinearLayout ll_city, layout;
    private TextView tv_city;
    private EditText et_search;


    private String searchStr = null;
    private Sort mSort = null;
    private SearchCityAdapter mAdapter = null;
    private ArrayList<String> newDataArrayList;
    private ArrayList<String> checkArrayList;
    private ArrayList<String> showArrayList;
    private String[] newData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        initView();
    }

    private void initView() {
        mContext = SelectCityActivity.this;

        et_search = (EditText) findViewById(R.id.et_city);
        ll_city = (LinearLayout) findViewById(R.id.ll_city);
        tv_city = (TextView) findViewById(R.id.tv_city);
        lv_cities = (ListView) findViewById(R.id.lv_cities);

        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    v.setAlpha(1);
                }else{
                    v.setAlpha(0.5f);
                }
            }
        });
        et_search.addTextChangedListener(new MyWatchToSearch());
        lv_cities.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (et_search.hasFocus()){
                    et_search.clearFocus();
                    et_search.setAlpha(0.5f);
                }
                return false;
            }
        });
        ll_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("city", tv_city.getText());
                setResult(RESULTCODE, intent);
                finish();
            }
        });

        mSort = new Sort();
        checkArrayList = new ArrayList<>();
        //得到排序后的数据（String[]）

        /*
        todo 这个排序会耗费大量的时间严重影响用户体验
        目前采用Cities.mCitiesStrings事先排好顺序
         */
        //newData = mSort.autoSort(Cities.mCitiesStrings);
        newData = Cities.mCitiesStrings;
        //加入A-Z 26个字母
        newDataArrayList = mSort.addChar(newData);
        showArrayList = newDataArrayList;
        mAdapter = new SearchCityAdapter(mContext, android.R.layout.simple_list_item_1,
                showArrayList);
        lv_cities.setAdapter(mAdapter);
        lv_cities.setOnItemClickListener(itemClickListener);


        layout = (LinearLayout) findViewById(R.id.letter_index);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1f);
        for (final char letter : Letters) {
            TextView tv = new TextView(mContext);
            tv.setText(String.valueOf(letter));
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params);
            tv.setMinHeight(10);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String index = ((TextView) v).getText().toString();
                    Log.d(TAG, "onClick.letter = " + index);
                    lv_cities.setSelection(getStartIndex(index));
                }
            });
            layout.addView(tv);
        }
    }

    private int getStartIndex(String letter) {
        int returnInt = 0;
        char index = 0;
        for (int i = 0; i < Letters.length; i++) {
            if (letter.equals(String.valueOf(Letters[i]))) {
                index = Letters[i];
                break;
            }
        }
        for (int j = 0; j < showArrayList.size(); j++) {
            String cityName = getPinYin(showArrayList.get(j));
            Log.d(TAG, "getStartIndex.cityName = " + cityName);
            if ((cityName.toCharArray())[0] == index) {
                returnInt = j;
                break;
            }
        }

        return returnInt;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick() called with " + "parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
            String choice = showArrayList.get(position);
            Log.d(TAG, "onItemClick.choice = " + choice);
            tv_city.setText(choice);
        }
    };

    private String getPinYin(String inputString) {
        StringBuffer output = new StringBuffer("");

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = new String[0];
                    temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output.append(temp[0]);
                    output.append(" ");
                } else
                    output.append(Character.toString(input[i]));
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
        }

        return output.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (et_search.hasFocus()){
            et_search.clearFocus();
            et_search.setAlpha(0.5f);
        }
        return false;
    }

    class MyWatchToSearch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            //搜索功能只针对ListView中的内容数据，不针对标题数据。
            //使用没添加的分组字母的数据创建新的ArrayList
            checkArrayList = mSort.toArrayList(Cities.mCitiesStrings);
            searchStr = et_search.getText().toString();

            if (searchStr.length() != 0) {
                checkSearchStr(searchStr);
                showArrayList = checkArrayList;
                mAdapter = new SearchCityAdapter(mContext,
                        android.R.layout.simple_list_item_1, showArrayList);
                lv_cities.setAdapter(mAdapter);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (searchStr.length() == 0) {
                showArrayList = newDataArrayList;
                mAdapter = new SearchCityAdapter(mContext,
                        android.R.layout.simple_list_item_1, showArrayList);
                lv_cities.setAdapter(mAdapter);
            }
            et_search.requestFocus();
        }

        /**
         *
         */
        public void checkSearchStr(String search) {

            String tempSearch;
            String tempList;
            String newDataChar = null;
            String checkArrayListItem = null;
            //当输入的搜索字符为字母时
            if (search.matches("[a-zA-Z]+")) {
                for (int i = 0; i < search.length(); i++) {
                    for (int j = 0; j < checkArrayList.size(); j++) {
                        checkArrayListItem = checkArrayList.get(j);
                        //如果当前取出的名称不为字母，则得到名称的所有首字母
                        if (!checkArrayListItem.matches("[a-zA-Z]+")) {
                            newDataChar = mSort
                                    .getAllPinYinHeadChar(checkArrayListItem);
                        } else {
                            newDataChar = checkArrayListItem;
                        }
                        //取出输入的字符串的第i个字母，并转换为大写
                        tempSearch = String.valueOf(search.charAt(i)).toUpperCase();
                        //取出得到的联系人名称所有首字母的第i个字母，并转换为大写
                        tempList = String.valueOf(newDataChar.charAt(i)).toUpperCase();

                        if (!(tempSearch.equals(tempList))) {
                            checkArrayList.remove(j);
                            newDataChar = null;
                            j--;
                        }
                    }
                }
            }
            //当输入的搜索字符为汉字时
            else if (search.matches("[\u4e00-\u9fa5]+")) {

                for (int j = 0; j < checkArrayList.size(); j++) {
                    if (!checkArrayList.get(j).contains(search)) {
                        checkArrayList.remove(j);
                        j--;
                    }
                }
            } else {
                search = String.valueOf(search.charAt(0));
                for (int j = 0; j < checkArrayList.size(); j++) {
                    if (!checkArrayList.get(j).contains(search)) {
                        checkArrayList.remove(j);
                        j--;
                    }
                }
            }
        }
    }
}
