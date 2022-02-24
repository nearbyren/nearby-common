/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ren.nearby.common_module.tinker.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.SystemClock;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.tinker.TinkerApplicationHelper;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

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
import ren.nearby.common_module.tinker.reporter.SampleTinkerReport;
import ren.nearby.common_module.tinker.util.TinkerManager;
import ren.nearby.common_module.tinker.util.Utils;


/**
 * optional, use dynamic configuration is better way
 * for native crash,
 * <p/>
 * Created by zhangshaowen on 16/7/3.
 * tinker's crash is caught by {@code LoadReporter.onLoadException}
 * use {@code TinkerApplicationHelper} api, no need to install tinker!
 */
public class SampleUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "Tinker.SampleUncaughtExHandler";

    private final Thread.UncaughtExceptionHandler ueh;
    private static final long QUICK_CRASH_ELAPSE = 10 * 1000;
    public static final int MAX_CRASH_COUNT = 3;
    private static final String DALVIK_XPOSED_CRASH = "Class ref in pre-verified class resolved to unexpected implementation";

    public SampleUncaughtExceptionHandler() {
        ueh = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        TinkerLog.e(TAG, "uncaughtException:" + ex.getMessage());
        rxLog("nearby", getStackTraceInfo(ex), ex);
        tinkerFastCrashProtect();
        tinkerPreVerifiedCrashHandler(ex);
        ueh.uncaughtException(thread, ex);
    }

    /**
     * 获取错误的信息
     */
    private String getStackTraceInfo(final Throwable throwable) {
        PrintWriter pw = null;
        Writer writer = new StringWriter();
        try {
            pw = new PrintWriter(writer);
            throwable.printStackTrace(pw);
        } catch (Exception e) {
            return "";
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return writer.toString();
    }

    public void rxLog(final @Nullable String tag, final @NotNull String message, final @Nullable Throwable t) {
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
                + File.separator + TinkerManager.getTinkerApplicationLike().getApplication().getPackageName() + File.separator + "log");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        FileOutputStream fos = new FileOutputStream(new File(dir, fileFormat.format(new Date())), true);
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();
    }

    /**
     * Such as Xposed, if it try to load some class before we load from patch files.
     * With dalvik, it will crash with "Class ref in pre-verified class resolved to unexpected implementation".
     * With art, it may crash at some times. But we can't know the actual crash type.
     * If it use Xposed, we can just clean patch or mention user to uninstall it.
     */
    private void tinkerPreVerifiedCrashHandler(Throwable ex) {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();
        if (applicationLike == null || applicationLike.getApplication() == null) {
            TinkerLog.w(TAG, "applicationlike is null");
            return;
        }

        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            TinkerLog.w(TAG, "tinker is not loaded");
            return;
        }

        Throwable throwable = ex;
        boolean isXposed = false;
        while (throwable != null) {
            if (!isXposed) {
                isXposed = Utils.isXposedExists(throwable);
            }

            // xposed?
            if (isXposed) {
                boolean isCausedByXposed = false;
                //for art, we can't know the actually crash type
                //just ignore art
                if (throwable instanceof IllegalAccessError && throwable.getMessage().contains(DALVIK_XPOSED_CRASH)) {
                    //for dalvik, we know the actual crash type
                    isCausedByXposed = true;
                }

                if (isCausedByXposed) {
                    SampleTinkerReport.onXposedCrash();
                    TinkerLog.e(TAG, "have xposed: just clean tinker");
                    //kill all other process to ensure that all process's code is the same.
                    ShareTinkerInternals.killAllOtherProcess(applicationLike.getApplication());

                    TinkerApplicationHelper.cleanPatch(applicationLike);
                    ShareTinkerInternals.setTinkerDisableWithSharedPreferences(applicationLike.getApplication());
                    return;
                }
            }
            throwable = throwable.getCause();
        }
    }

    /**
     * if tinker is load, and it crash more than MAX_CRASH_COUNT, then we just clean patch.
     */
    private boolean tinkerFastCrashProtect() {
        ApplicationLike applicationLike = TinkerManager.getTinkerApplicationLike();

        if (applicationLike == null || applicationLike.getApplication() == null) {
            return false;
        }
        if (!TinkerApplicationHelper.isTinkerLoadSuccess(applicationLike)) {
            return false;
        }

        final long elapsedTime = SystemClock.elapsedRealtime() - applicationLike.getApplicationStartElapsedTime();
        //this process may not install tinker, so we use TinkerApplicationHelper api
        if (elapsedTime < QUICK_CRASH_ELAPSE) {
            String currentVersion = TinkerApplicationHelper.getCurrentVersion(applicationLike);
            if (ShareTinkerInternals.isNullOrNil(currentVersion)) {
                return false;
            }

            SharedPreferences sp = applicationLike.getApplication().getSharedPreferences(ShareConstants.TINKER_SHARE_PREFERENCE_CONFIG, Context.MODE_MULTI_PROCESS);
            int fastCrashCount = sp.getInt(currentVersion, 0) + 1;
            if (fastCrashCount >= MAX_CRASH_COUNT) {
                SampleTinkerReport.onFastCrashProtect();
                TinkerApplicationHelper.cleanPatch(applicationLike);
                TinkerLog.e(TAG, "tinker has fast crash more than %d, we just clean patch!", fastCrashCount);
                return true;
            } else {
                sp.edit().putInt(currentVersion, fastCrashCount).commit();
                TinkerLog.e(TAG, "tinker has fast crash %d times", fastCrashCount);
            }
        }

        return false;
    }
}
