package ren.nearby.common.di;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import ren.nearby.common_module.http.HttpModule;
import ren.nearby.common_module.tinker.Log.MyLogImp;
import ren.nearby.common_module.tinker.util.TinkerManager;
import timber.log.Timber;

//import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Administrator on 2018/4/26 0026.
 */
public class BaseApplicationLike extends DefaultApplicationLike {


    public BaseApplicationLike(
            Application application,
            int tinkerFlags,
            boolean tinkerLoadVerifyFlag,
            long applicationStartElapsedTime,
            long applicationStartMillisTime,
            Intent tinkerResultIntent) {
        super(
                application,
                tinkerFlags,
                tinkerLoadVerifyFlag,
                applicationStartElapsedTime,
                applicationStartMillisTime,
                tinkerResultIntent);
    }


    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        MultiDex.install(base);

        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());


    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    private static DiComponent diComponent;

    public DiComponent getDiComponent() {
        return diComponent;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLog();
//        Bugly.init(getApplication(), "900029763", true);
        //建议在测试阶段建议设置成true，发布时设置为false。
        CrashReport.initCrashReport(getApplication(), "2c9ed21abc", false);
        initLeakCanary();
        initStetho();
//        initDagger2();
        x5Web();
        initARouter();
        initHttp();

    }

    private void initHttp() {
//        RxRetrofitClient.Builder builder =
//                new RxRetrofitClient.Builder()
//                        .connectTimeOut(1000);
//        RxRetrofitClient.init(builder);
    }

    public void initARouter() {
        ARouter.openLog();
        ARouter.openDebug();
        ARouter.init(getApplication());
    }


    public void x5Web() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Logger.d(" onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplication(), cb);
    }

    /**
     * dagger2注入
     */
    public void initDagger2() {
//        DaggerDiComponent
//                .builder()
//                .httpModule(new HttpModule(getApplication(), 0))
//                .build()
//                .inject((DiBaseApplication) getApplication());

    }

    /**
     * 网络请求分析
     */
    public void initStetho() {
        Stetho.initializeWithDefaults(getApplication());
        //配置okhttp库的网络请求分析 我们打开Chrome，在地址栏输入chrome://inspect/
        //new OkHttpClient.Builder()
        //.addNetworkInterceptor(new StethoInterceptor())
        //.build()
    }

    /**
     * 内存泄漏检测
     */
    public void initLeakCanary() {
//        if (LeakCanary.isInAnalyzerProcess(getApplication())) {
//            return;
//        }
//        LeakCanary.install(getApplication());
    }

    /**
     * 初始化输出日志配置
     */
    public void initLog() {
        Timber.plant(new CrashReportingTree());
        FormatStrategy formatStrategy = PrettyFormatStrategy
                .newBuilder()
                .showThreadInfo(true)
//                .methodCount(5)
//                .methodOffset(7)
//                .logStrategy()
                .tag(tag)
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));

    }

    /**
     * 自定义异常日志
     */
    class CrashReportingTree extends Timber.Tree {
        /**
         * 是否记录log
         *
         * @param tag      tag
         * @param priority 级别
         * @return true 往下走log，否则不走
         */
        @Override
        protected boolean isLoggable(@Nullable String tag, int priority) {
            return priority >= Log.INFO;
        }

        /**
         * 自己处理对应的日志信息
         *
         * @param priority 级别
         * @param tag      tag
         * @param message  message
         * @param t        错误信息
         */
        @Override
        protected void log(int priority, @Nullable final String tag, @NotNull final String message, @Nullable final Throwable t) {
            /* 如果日志界别是v或d就不做任何处理 */
//            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
//                return;
//            }
            logcat(tag, message, t);
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) {
                    try {
                        saveLogcat(tag, message, t);
                        emitter.onNext(true);
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DefaultObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean aBoolean) {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });

        }

        /**
         * 打印日志
         */
        @SuppressLint("LogNotTimber")
        private void logcat(@Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            if (tag == null) {
                if (t == null) {
                    Log.e(tag, message);
                } else {
                    Log.e(tag, message, t);
                }
            } else {
                if (t == null) {
                    Log.e(tag, message);
                } else {
                    Log.e(tag, message, t);
                }
            }
        }

        /**
         * 保存日志
         */
        @SuppressLint("LogNotTimber")
        private void saveLogcat(@Nullable String tag, @NotNull String message, @Nullable Throwable t) throws Exception {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sb.append(sdf.format(new Date())).append("  日志：");
            sb.append("[").append(tag).append("] {").append(message).append("}\n");
            if (t != null) {
                Writer writer = new StringWriter();
                PrintWriter pw = new PrintWriter(writer);
                t.printStackTrace(pw);

                Throwable cause = t.getCause();
                // 循环着把所有的异常信息写入writer中
                while (cause != null) {
                    cause.printStackTrace(pw);
                    cause = cause.getCause();
                }
                pw.close();// 记得关闭
                String result = writer.toString();
                sb.append("错误信息：").append("\n").append(result);
            }

            // 目录
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + getApplication().getPackageName() + File.separator + "log");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            FileOutputStream fos = new FileOutputStream(new File(dir, fileFormat.format(new Date())), true);
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        }

    }

    private String tag = "nearby";


}
