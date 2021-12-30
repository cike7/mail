package com.zhuli.mail.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.util.WifiContentUtil;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2021/12/30
 * Description: 监听wifi状态广播接收器
 * Author: zl
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private WifiManager mWifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //wifi开关变化
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED: {
                    //wifi关闭
                    LogInfo.e("打开变化：wifi已经关闭");
                    WifiContentUtil.openWifi(mWifiManager);
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    //wifi正在关闭
                    LogInfo.e("打开变化：wifi正在关闭");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    //wifi已经打开
                    LogInfo.e("打开变化：wifi已经打开");
                    WifiContentUtil.startScanWifi(mWifiManager);
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    //wifi正在打开
                    LogInfo.e("打开变化：wifi正在打开");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    //未知
                    LogInfo.e("打开变化：wifi未知状态");
                    break;
                }
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //监听wifi连接状态
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            LogInfo.e("--NetworkInfo--" + info.toString());
            if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                LogInfo.e("连接状态：wifi没连接上");

            } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                LogInfo.e("wifi已连接");
            } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                LogInfo.e("连接状态：wifi正在连接");
            }
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            //监听wifi列表变化
            LogInfo.e("wifi列表发生变化");
        }
    }

}
