package com.ax.vertu.weatheralarmclock.tools;

/**
 * Created by VERTU on 2017/1/21.
 */

import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Sort {

    /**
     * 中英文混合排序
     *
     * @param strArr 原始的通讯录名单
     * @return 排序后的通讯录名单
     */
    public String[] autoSort(String[] strArr) {
        String temp = "";

        String headchar1;

        String headchar2;

        for (int i = 0; i < strArr.length; i++) {
            for (int j = i; j < strArr.length; j++) {
                headchar1 = getPinYinHeadChar(strArr[i]).toUpperCase();
                headchar2 = getPinYinHeadChar(strArr[j]).toUpperCase();
                if (headchar1.charAt(0) > headchar2.charAt(0)) {
                    temp = strArr[i];
                    strArr[i] = strArr[j];
                    strArr[j] = temp;
                }
            }
        }
        return strArr;
    }

    /**
     * 得到当前联系人名称的的一个汉字的首字母
     *
     * @param str 联系人名称
     * @return 首字母
     */
    public String getPinYinHeadChar(String str) {
        String convert = "";
        char word = str.charAt(0);
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
        if (pinyinArray != null)
            convert = String.valueOf(pinyinArray[0].charAt(0));
        else {
            convert = String.valueOf(word);
        }
        return convert;
    }

    /**
     * 实现数据分组
     *
     * @param strArr 排序后的通讯录名单
     * @return 对数据添加了分组字母的ArrayList（如在所有首字母为B的名单组之前添加一个B实现分组）
     */
    public ArrayList<String> addChar(String[] strArr) {
        String headchar = "";
        ArrayList<String> list = new ArrayList<String>();
        int j = 0;
        for (int i = 0; i < strArr.length; i++) {
            headchar = String.valueOf(getPinYinHeadChar(strArr[i]).charAt(0)).toUpperCase();
            if (!list.contains(headchar)) {
                list.add(headchar);
                list.add(strArr[i]);
            } else {
                list.add(strArr[i]);
            }
        }
        return list;
    }


    public ArrayList<String> toArrayList(String[] str) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < str.length; i++)
            arrayList.add(str[i]);
        return arrayList;

    }

    /**
     * @param str 联系人名称
     * @return 联系人名称中每个汉字的首字母
     */
    public String getAllPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }
}
