package com.zhuli.mail.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.receiver.WifiBroadcastReceiver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2021/12/30
 * Description:
 * Author: zl
 */
public class WifiContentUtil {

    private static SpareWifi spareWifi;

    /**
     * 监听wifi变化
     *
     * @param context
     */
    public static SpareWifi registerReceiverWifi(Context context) {
        WifiBroadcastReceiver wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        //监听wifi是开关变化的状态
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //监听wifi连接状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //监听wifi列表变化（开启一个热点或者关闭一个热点）
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiReceiver, filter);
        spareWifi = new SpareWifi();
        return spareWifi;
    }


    /**
     * 开启wifi
     */
    public static void openWifi(WifiManager mWifiManager) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }


    /**
     * 开始扫描wifi
     */
    public static void startScanWifi(WifiManager manager) {
        if (manager != null) {
            manager.startScan();
            if (spareWifi.getWifiName().equals("") || spareWifi.getWifiPassword().equals("")) {
                return;
            }
            connectWifi(manager, spareWifi.getWifiName(), spareWifi.getWifiPassword(), "WPA");
        }
    }


    /**
     * 获取wifi加密方式
     */
    public static String getEncrypt(WifiManager mWifiManager, ScanResult scanResult) {
        if (mWifiManager != null) {
            String capabilities = scanResult.capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "WPA";
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    return "WEP";
                } else {
                    return "没密码";
                }
            }
        }
        return "获取失败";
    }


    /**
     * 连接WiFi
     *
     * @param wifiManager
     * @param wifiName
     * @param password
     * @param type
     */
    @SuppressLint({"WifiManagerLeak", "MissingPermission"})
    public static void connectWifi(WifiManager wifiManager, String wifiName, String password, String type) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + wifiName + "\"";
        String psd = "\"" + password + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (type) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            default:
                //无密码
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        wifiManager.addNetwork(conf);
        //TODO 新 ：WifiNetworkSuggestion

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }


    /**
     * 断开连接
     */
    public static boolean disconnectNetwork(WifiManager manager) {
        return manager != null && manager.disconnect();
    }


    /**
     * 关闭wifi
     */
    public static void closeWifi(WifiManager mWifiManager) {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }


    /**
     * 保存网络
     */
    public static void saveNetworkByConfig(WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method save = manager.getClass().getDeclaredMethod("save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (save != null) {
                save.setAccessible(true);
                save.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 备用WiFi
     */
    public static class SpareWifi {

        private String wifiName = "", wifiPassword = "";

        /**
         * 设置备用WiFi ,应该保证这个是正确的
         *
         * @param ssid
         * @param password
         */
        public void setSpareWifi(String ssid, String password) {
            wifiName = ssid;
            wifiPassword = password;
        }

        protected String getWifiName() {
            return wifiName;
        }

        protected String getWifiPassword() {
            return wifiPassword;
        }

    }

}
