package com.vaj.airsnapp;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

public class Config {

    static public final String APP_VERSION="1.00";

    static public final int UID=1;
    static public final String BASE="http://vikashanandjha.com/";
    // static public final String BASE="http://192.168.43.30/";
    static public final String BASE_URL=BASE+"/APIv2/";
    static public final String BASE_IMAGE_URL=BASE+"/uploads/images/";


    public final static String PKG_NAME = "com.vaj.airsnapp";


    static public final String APP_PKG_ID=PKG_NAME;

    public static void showToast(Context ct, String msg)
    {
        Toast.makeText(ct,msg,Toast.LENGTH_SHORT).show();

    }

    public static Long getradomNumber()
    {
        Random r = new Random();
        int low = 10;
        int high = 10000;
        int result = r.nextInt(high-low) + low;
        return result+System.currentTimeMillis();
    }
}
