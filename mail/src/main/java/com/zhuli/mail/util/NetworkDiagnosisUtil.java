package com.zhuli.mail.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.zhuli.mail.mail.LogInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description: 网络检查工具，检查当前网络是否可用
 * Author: zl
 */
public class NetworkDiagnosisUtil {

    private static WeakReference<Context> mContext;

    private static boolean nowNetworkConnectState = false;

    public static boolean getNetworkState() {
        return nowNetworkConnectState;
    }

    /**
     * 初始化网络诊断
     *
     * @param context
     * @return
     */
    public static void init(Context context) {
        if (mContext == null) {
            mContext = new WeakReference<>(context);
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            NetworkCallbackImpl callback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            manager.registerNetworkCallback(request, callback);
            callback.setCallable(new NetworkCallbackImpl.NetworkCall() {
                @Override
                public void call(boolean connectState) {
                    if (!connectState && ping()) {
                        nowNetworkConnectState = false;
                        notNetworkConnect();
                    } else {
                        nowNetworkConnectState = true;
                    }
                }
            });
        } else {
            NetworkInfo mNetworkInfo = manager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                if (!mNetworkInfo.isAvailable() || ping()) {
                    notNetworkConnect();
                }
            }
        }

    }


    /**
     * 没有网络连接处理
     */
    public static void notNetworkConnect() {
        if (mContext != null) {
            Toast.makeText(mContext.get(), "网络连接断开！", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext.get());
            builder.setMessage("当前网络不可以，请检查你的网络设置！").setTitle("提示")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.get().startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }


    /**
     * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     *
     * @return
     * @category
     */
    public static boolean ping() {

        InputStream input = null;
        BufferedReader reader = null;
        try {
            // ping 的地址，可以换成任何一种可靠的外网
            String ip = "www.baidu.com";
            // ping网址3次
            Process p = Runtime.getRuntime().exec("ping -c 10 -w 100 " + ip);
            // 读取ping的内容，可以不加
            input = p.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuffer = new StringBuilder();
            String content;
            while ((content = reader.readLine()) != null) {
                stringBuffer.append(content);
            }
            // ping的状态
            return p.waitFor() != 0;

        } catch (IOException | InterruptedException ignored) {

        } finally {
            if (input != null && reader != null) {
                try {
                    input.close();
                    reader.close();
                } catch (IOException ignored) {

                }
            }
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

        private NetworkCall callable;

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            if (callable != null) {
                callable.call(true);
            }
            LogInfo.e("网络已连接!");
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            if (callable != null) {
                callable.call(false);
            }
            LogInfo.e("网络断开!");
        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (callable != null) {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        LogInfo.e("网络发送改变 wifi已经连接");
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        LogInfo.e("网络发送改变 数据流量已经连接");
                    } else {
                        LogInfo.e("网络发送改变 其他网络");
                    }
                }
            }
        }


        /**
         * 监听连接状态
         *
         * @return
         */
        public void setCallable(NetworkCall callable) {
            this.callable = callable;
        }

        public interface NetworkCall {
            void call(boolean connectState);
        }

    }

}
