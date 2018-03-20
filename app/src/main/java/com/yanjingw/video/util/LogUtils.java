package com.yanjingw.video.util;

import android.util.Log;

/**
 * Created by wangyanjing on 2017/12/25.
 */

public class LogUtils {


    public static final String YANJINGW = "yanjingw_log";

    public static void i(String message) {
        Log.i(YANJINGW, message);
    }

    public static void v(String message) {
        Log.v(YANJINGW, message);
    }

    public static void e(String message) {
        Log.e(YANJINGW, message);
    }

    public static void d(String message) {
        Log.d(YANJINGW, message);
    }
}
