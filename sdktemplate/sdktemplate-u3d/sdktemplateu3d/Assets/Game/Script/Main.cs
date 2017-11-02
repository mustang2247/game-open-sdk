using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Main : MonoBehaviour
{
    //请输入一个字符串
    private string stringToEdit = "Please enter a string";

    // Use this for initialization
    void Start()
    {
        print(SystemInfo.batteryLevel);
        print(SystemInfo.batteryStatus);

        print(SystemInfo.deviceModel);
        print(SystemInfo.deviceName);
        print(SystemInfo.deviceType);
        print(SystemInfo.deviceUniqueIdentifier);

        print(SystemInfo.operatingSystem);
        print(SystemInfo.operatingSystemFamily);
        print(SystemInfo.systemMemorySize);
    }

    void Update()
    {
        //点击手机返回键关闭应用程序
        if (Input.GetKeyDown(KeyCode.Escape) || Input.GetKeyDown(KeyCode.Home))
        {
            Application.Quit();
        }
    }

    void OnGUI()
    {
        //绘制一个输入框接收用户输入
        stringToEdit = GUILayout.TextField(stringToEdit, GUILayout.Width(500), GUILayout.Height(300));

        //一个提交按钮
        if (GUILayout.Button("commit", GUILayout.Height(50)))
        {
            //注解1
            using (AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            {
                using (AndroidJavaObject jo = jc.GetStatic<AndroidJavaObject>("currentActivity"))
                {
                    //调用Android插件中UnityTestActivity中StartActivity0方法，stringToEdit表示它的参数
                    jo.Call("StartActivity0", stringToEdit);
                }
            }
        }
    }

    //注解2
    void messgae(string str)
    {
        stringToEdit = str;
    }
}