package com.news_success.utils;

import android.os.Environment;

import com.blankj.utilcode.utils.FileUtils;
import com.blankj.utilcode.utils.TimeUtils;
import com.news_success.BuildConfig;
import com.news_success.common.Constants;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2017/1/16 0016.
 */

public class MyUtils {
    /**
     * 向文件写入信息
     */
    public static void writeLog(String FileNameDir,String FileName,String info){
        if (!BuildConfig.DEBUG){//上线模式就不打印了
            return;
        }
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.APP_NAME + File.separator + FileNameDir + File.separator;
        String name=FileName+ TimeUtils.getCurTimeString(new SimpleDateFormat("yy-MM-dd"))+".txt";
        String content=TimeUtils.getCurTimeString(new SimpleDateFormat("HH:mm:ss"))+info+"\r\n";
        FileUtils.createOrExistsDir(dir);
        FileUtils.createOrExistsFile(name);
        FileUtils.writeFileFromString(dir+name,content,true);
    }


}
