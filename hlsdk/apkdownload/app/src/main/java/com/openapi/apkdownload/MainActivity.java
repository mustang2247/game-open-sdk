package com.openapi.apkdownload;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.openapi.apkdownload.cb.ChannelInterfaceProxy;
import com.openapi.apkdownload.cb.OnProgressListener;
import com.openapi.apkdownload.tools.UpdateSdkUtil;
import com.openapi.apkdownload.tools.UpdateService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openApiGetdownload();
            }
        });
    }

    public void openApiGetdownload() {
        Log.i(Constants.tag, "openApiGetdownload ok");
        try {
            String json = "{\"updateVersion\":\"2.1.0\",\"updateType\":\"2\",\"packageName\":\"com.hule.fishing\",\"updateUrl\":\"http://huleshikongres-10034783.file.myqcloud.com/app.apk\",\"updateMsg\":\"2.1.0\"}";
            // 版本更新处理
            UpdateSdkUtil.updateSdkVersion(this, "2.0.0", json, "测试下载", new ChannelInterfaceProxy.ApplicationInitCallback() {
                @Override
                public void execute() {
                    Log.i(Constants.tag, "加载apk失败，参数不正确！！！！！");
                }
            });
        } catch (Exception e) {
            Log.i(Constants.tag, "openApiGetdownload err");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
