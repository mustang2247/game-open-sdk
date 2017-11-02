package com.opensdktemplate.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.opensdktemplate.R
import com.unity3d.player.UnityPlayer

class Activity0 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_0)

        setContentView(R.layout.activity_main)

        val text = this.findViewById<View>(R.id.textView1) as TextView
        text.text = this.intent.getStringExtra("name")

        val edit = this.findViewById<View>(R.id.edit) as EditText

        val close = this.findViewById<View>(R.id.button0) as Button
        close.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                //android 调unity的接口
                UnityPlayer.UnitySendMessage("Main Camera", "messgae", edit.text.toString())
                this@Activity0.finish();
            }
        })
    }
}
