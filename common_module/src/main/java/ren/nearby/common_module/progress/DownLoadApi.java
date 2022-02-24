package ren.nearby.common_module.progress;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Administrator on 2016/5/26.
 */
public interface DownLoadApi {
    /**
     * apk下载版本
     *
     * @return
     */
    @GET("download.php?")
    Call<ResponseBody> downloadApk(@QueryMap Map<String, String> dynamic);
}
