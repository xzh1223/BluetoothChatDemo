package com.xzh.bluetoothchatdemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhenghangxia on 17-5-23.
 *
 *      Toast提示工具类
 */

public class ToastUtil {

    private static Toast mToast;
    private static long oneTime;
    private static long twoTime;
    private static String oldMsg;

    public static void toast(Context context, String strings) {

        if(mToast==null){
            mToast =Toast.makeText(context, strings, Toast.LENGTH_SHORT);
            mToast.show();
            oneTime=System.currentTimeMillis();
        }else{
            twoTime=System.currentTimeMillis();
            if(strings.equals(oldMsg)){
                if(twoTime-oneTime>Toast.LENGTH_SHORT){
                    mToast.show();
                }
            }else{
                oldMsg = strings;
                mToast.setText(strings);
                mToast.show();
            }
        }
        oneTime=twoTime;

        /*if (mToast == null) {
            mToast = Toast.makeText(context, strings, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(strings);
        }
        mToast.show();*/
    }

}
