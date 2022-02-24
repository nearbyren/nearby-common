package ren.nearby.common_module.progress;

import com.orhanobut.logger.Logger;

import org.reactivestreams.Subscription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import ren.nearby.common_module.event.RxBus2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class FileCallback implements Callback<ResponseBody> {

    /**
     * 订阅下载进度
     */
    private CompositeDisposable rxSubscriptions = new CompositeDisposable();

    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public FileCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        subscribeLoadProgress();
    }

    public void onSuccess(File file) {
        unsubscribe();
    }

    public abstract void start(int a);

    public abstract void progress(long contentLength, long progress, long total);

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            saveFile(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     *
     * @param response
     * @return
     * @throws IOException
     */
    public File saveFile(Response<ResponseBody> response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = response.body().byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            onSuccess(file);
            return file;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 订阅文件下载进度
     */
    private void subscribeLoadProgress() {
        Logger.d("subscribeLoadProgress");
        rxSubscriptions.add(

                RxBus2.getDefault()
                        .toFlowable(FileLoadEvent.class)
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                start(1);
                            }
                        })
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(Subscription subscription) throws Exception {
                                subscription.request(Long.MAX_VALUE);
                            }
                        })
                        .onBackpressureDrop()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<FileLoadEvent>() {
                            @Override
                            public void accept(FileLoadEvent fileLoadEvent) throws Exception {
                                Logger.d("---------------2---------------");
                                progress(fileLoadEvent.contentLength, fileLoadEvent.getProgress(), fileLoadEvent.getTotal());
                            }
                        }));


    }


    /**
     * 取消订阅，防止内存泄漏
     */
    private void unsubscribe() {
        if (!rxSubscriptions.isDisposed()) {
            rxSubscriptions.dispose();
        }
    }

}
