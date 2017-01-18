package com.news_success;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.StrictMode;

import com.blankj.utilcode.utils.LogUtils;
import com.news_success.common.Constants;
import com.news_success.utils.MyUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 1.初始化LeakCanary
 * 2.初始化LogUtils  将log日志保存到本地
 * 3.监控正在使用的Activity生命周期 并将日志保存在本地
 * 4.使用严格模式
 * 5.保存异常崩溃的信息到本地
 * 6.初始化日间模式 或 夜间 模式
 *
 */

public class App extends Application implements Thread.UncaughtExceptionHandler{
    private static App mApp;
    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initLeakCanary();
        LogUtils.init(this, BuildConfig.DEBUG, true, 'v', "MyTag");
        initActivityLifecycleLogs();
        initStrickMode();//使用严格模式
        Thread.setDefaultUncaughtExceptionHandler(this);
        //todo 日/夜 间模式
        //// TODO: 2017/1/16 0016  greenDAO 的数据初始化

    }

    /**
     * 使用严格模式
     */
    private void initStrickMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyDialog()//弹出违规提示对话框
                            .penaltyLog()
                            .build()
            );
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    /**
     * 监控正在使用的Activity生命周期
     */
    private void initActivityLifecycleLogs() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                LogUtils.i("ActivityLife", activity + " : onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.i("ActivityLife", activity + " : onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.i("ActivityLife", activity + " : onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.i("ActivityLife", activity + " : onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.i("ActivityLife", activity + " : onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                LogUtils.i("ActivityLife", activity + " : onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.i("ActivityLife", activity + " : onActivityDestroyed");
            }
        });
    }

    /**
     * 初始化LeakCanary
     */
    private void initLeakCanary() {
        if (BuildConfig.DEBUG) {
            mRefWatcher = LeakCanary.install(this);//调试版本采用这个方法
        } else {
            mRefWatcher = RefWatcher.DISABLED;//正式版本采用这个方法
        }
    }

    public synchronized static Application getApp() {
        if (mApp == null) {
            mApp = new App();
        }
        return mApp;
    }

    /**
     * 捕获全局异常变量 并保存到本地
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();
        ByteArrayOutputStream baos=null;
        PrintStream printStream=null;
        String info=null;
        try{
            baos=new ByteArrayOutputStream();
            printStream=new PrintStream(baos);
            throwable.printStackTrace(printStream);
            info=new String(baos.toByteArray());
        }catch (Exception e){
            e.printStackTrace();
            LogUtils.e(e.getCause());
        }finally {
            try {
                if (printStream!=null){
                    printStream.close();
                }
                if (baos!=null){
                    baos.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // 捕获闪退的异常,并保存到本地
        MyUtils.writeLog(Constants.ERROR_LOG,Constants.ERROR_LOG,info);

    }
}
