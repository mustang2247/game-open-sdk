package com.openapi.template.activity;

import android.content.Intent;

import com.stvgame.ysdk.activity.SplashScreen2Activity;

/**
 * Created by JGLOR￥ on 2018/3/7.
 */

public class SplashActivity extends SplashScreen2Activity {

    @Override
    public void toMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
