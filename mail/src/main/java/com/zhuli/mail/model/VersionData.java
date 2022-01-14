package com.zhuli.mail.model;

/**
 * @Description 版本更新数据
 * @Author zhuli
 * @Date 2021/6/7/3:55 PM
 */
public class VersionData {

    private String version;
    private String url;
    private String content;

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
