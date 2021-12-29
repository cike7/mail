package com.zhuli.mail.file;

import com.zhuli.mail.unit.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description:
 * Author: zl
 */
public class ProcessingZipFile implements ActingProcessing {

    @Override
    public void startProcessingFile(String path, FileProcessingCompleteListener listener) {
        try {
            List<String> unzipPaths = new ArrayList<>();
            String[] paths = path.split("/");
            //在原文件目录下新建文件夹，去掉后缀名
            String destDirPath = path.replace(".zip", "");
            new File(destDirPath).delete();
            new File(destDirPath).mkdir();
            List<File> files = ZipUtils.unzipFile(path, destDirPath);
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < paths.length - 1; i++) {
                stringBuffer.append(paths[i] + "/");
            }
            for (int i = 0; i < files.size(); i++) {
                unzipPaths.add(stringBuffer.toString() + files.get(i).getPath());
            }

            listener.onComplete(unzipPaths);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
