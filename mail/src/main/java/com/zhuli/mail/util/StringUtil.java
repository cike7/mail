package com.zhuli.mail.util;

import com.zhuli.mail.mail.LogInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 获取字符串中的url
     *
     * @param str
     * @return
     */
    public static String getUrl(String str) {
        Pattern pattern = null;
        if (str.contains("https")) {
            pattern = Pattern.compile("https://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
        } else if (str.contains("http")) {
            pattern = Pattern.compile("http://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
        }

        if (pattern == null) return null;

        Matcher matcher = pattern.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group()).append("\r\n");
        }
        return buffer.toString();
    }


    /**
     * 获取字符串中的文件大小
     *
     * @param str [10.0MB]
     * @return 10.0MB
     */
    public static StringBuffer getSegment(String str) {
        Pattern pattern = Pattern.compile("[0-9]+[.]+[^\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer buffer = new StringBuffer();
        if (matcher.find()) {
            buffer.append(matcher.group()).append("MB");
        }
        return buffer;
    }

    /**
     * 获取字符串中的文件名
     * @param str
     * @return
     */
    public static String getFileName(String str){
        String[] strings = str.split(" ");
        for (int i = 0; i < FileMime.MIME_MapTable.length; i++) {
            String fileType = FileMime.MIME_MapTable[i][0];
            if (!fileType.equals("")) {
                for (String string : strings) {
                    if (string.endsWith(fileType)) {
                        LogInfo.e("文件类型为：" + fileType + "\n" + string);
                        return string;
                    }
                }
            }
        }
        return "";
    }

}
