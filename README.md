v1.0.2

# 主程序app模块里build.gradle 里面添加以下内容（选择 JavaMail 许可文件,忽略其他重复文件,解决Android 11 及一下版本许可证重复问题）

android {
    ***
    packagingOptions {
        pickFirst 'META-INF/LICENSE.md'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/NOTICE.md'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/ASL2.0'
        exclude("META-INF/*.kotlin_module")
    }
    ***
}