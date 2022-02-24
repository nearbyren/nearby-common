package ren.nearby.common_module.progress;

/**
 * Created by Administrator on 2016/9/1.
 */
public class FileLoadEvent {
    /**
     * 文件大小
     */
    long total;
    /**
     * 已下载大小
     */
    long progress;
    //长度
    long contentLength;

    public long getProgress() {
        return progress;
    }

    public long getTotal() {
        return total;
    }

    public long getContentLength() {
        return contentLength;
    }

    public FileLoadEvent(long contentLength, long total, long progress) {
        this.contentLength = contentLength;
        this.total = total;
        this.progress = progress;
    }
}
