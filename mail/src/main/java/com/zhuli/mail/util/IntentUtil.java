package com.zhuli.mail.util;

import android.content.Intent;

public class IntentUtil {

    /**
     * 获取文件Intent
     *
     * @return
     */
    public static Intent getFileIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // 所有类型
        intent.setType("*/*");
        //默认类别
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //允许多个
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //标志授予读取 URI 权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

}
