package com.opensdktemplate.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/9/6.
 */
public class DownTask extends AsyncTask<Void, Integer, String> {

    private static final int connectTimeout = 12000; /* milliseconds */
    private ProgressDialog progressDialog;
    private int hasRead = 0;
    private Context mContext;
    private String path;

    public DownTask(Context context, String path) {
        this.mContext = context;
        this.path = path;
    }

    @Override
    protected String doInBackground(Void... params) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                hasRead++;
                publishProgress(hasRead);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        progressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("下载中");
        progressDialog.setMessage("任务正在下载中...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(202);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }
}
