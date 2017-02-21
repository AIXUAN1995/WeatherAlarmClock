package com.ax.vertu.weatheralarmclock.tools;

import android.app.Activity;

/**
 * Created by VERTU on 2017/1/26.
 */
public class MyActivityClass {
    private Class<? extends android.app.Activity> MyActivityClass;

    public MyActivityClass(Class<? extends android.app.Activity> MyActivityClass) {
        this.MyActivityClass = MyActivityClass;
    }

    public Class<? extends Activity> getMyActivityClass() {
        return MyActivityClass;
    }

    public void setMyActivityClass(Class<? extends Activity> myActivityClass) {
        MyActivityClass = myActivityClass;
    }
}
