package ren.nearby.common_module.progress;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import ren.nearby.common_module.event.RxBus2;


/**
 * Created by Administrator on 2016/5/26.
 */
public class ProgressResponseBody2 extends ResponseBody {

    private final Response response;

    private BufferedSource bufferedSource;

    private long contentLength = 0L;

    public ProgressResponseBody2(Response response) {
        this.response = response;

    }

    @Override
    public MediaType contentType() {
        return response.body().contentType();
    }

    @Override
    public long contentLength() {
        return response.body().contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource) {
            bufferedSource = Okio.buffer(source(response.body().source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                long bytesRead = super.read(sink, byteCount);
//                Logger.d("进度条 - bytesRead = " + bytesRead);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                RxBus2.getDefault().post(new FileLoadEvent(contentLength, contentLength(), totalBytesRead));

                return bytesRead;
            }
        };
    }
}
