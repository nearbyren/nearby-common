package ren.nearby.common_module.http;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import ren.nearby.common_module.progress.ProgressResponseBody2;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/9/1 0001.
 *  @Inject 注解构造 生成“大众”工厂类
 *  或者
 *  @Module +@Providers 提供注入“私有”工厂类
 *  通过Component 创建获得Activity,获得工厂类Provider，统一交给Injector
 *  Injector将Provider的get()方法提供的对象，注入到Activity容器对应的成员变量中，
 *  我们就可以直接使用Activity容器中对应的成员变量了！
 */
@Module
public class HttpModule {

    private static final String F_BREAK = " %n";
    private static final String F_URL = " %s";
    private static final String F_TIME = " in %.1fms";
    private static final String F_HEADERS = "%s";
    private static final String F_RESPONSE = F_BREAK + "Response: %d";
    private static final String F_BODY = "body: %s";

    private static final String F_BREAKER = F_BREAK + "-------------------------------------------" + F_BREAK;
    private static final String F_REQUEST_WITHOUT_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS;
    private static final String F_RESPONSE_WITHOUT_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BREAKER;
    private static final String F_REQUEST_WITH_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS + F_BODY + F_BREAK;
    private static final String F_RESPONSE_WITH_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BODY + F_BREAK + F_BREAKER;

    private final Application baseApplication;
    private final int mKey;//证书key

    public HttpModule(Application context, int key) {
//        Logger.d(context == null ? "上下文为空" : "上下文不为空");
//        Logger.d(key == 0 ? "证书文件为空" : "证书文件不为空");
        this.baseApplication = context;
        mKey = key;
    }
//    public HttpModule(){}

    @Provides
    @Singleton
    Context provideContext() {
        return baseApplication;
    }

    /**
     * 1.构建retrofit
     */
    @Provides
    @Singleton
    protected HttpApi provideHttpApi(Retrofit retrofit) {
        return retrofit.create(HttpApi.class);
    }

    public String stringifyResponseBody(String responseBody) {
        return responseBody;
    }

    private static String stringifyRequestBody(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    /**
     * 拦截实现多个baseUrl切换问题
     */
    class MoreBaseUrlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取原始的originalRequest
            Request request = chain.request();
            //获取老的url
            HttpUrl oldUrl = request.url();
            //获取originalRequest的创建者builder
            Request.Builder builder = request.newBuilder();
            //获取头信息的集合如：manage,mdffx
            List<String> urlNames = request.headers("urlName");
            if (urlNames != null && urlNames.size() > 0) {
                builder.removeHeader("urlName");
                String urlName = urlNames.get(0);
                HttpUrl baseUrl = null;
                //根据头信息中配置的value,来匹配新的base_url地址
                if ("manage".equals(urlName)) {
                    baseUrl = HttpUrl.parse(HttpApi.BASE_URL);
                } else if ("mdffx".equals(urlName)) {
                    baseUrl = HttpUrl.parse(HttpApi.BASE_URL2);
                }
                HttpUrl newHttpUrl = oldUrl
                        .newBuilder()
                        .scheme(baseUrl.scheme())//http协议如：http或者https
                        .host(baseUrl.host())//主机地址
                        .port(baseUrl.port())//端口
                        .build();
                //获取处理后的新newRequest
                Request newRequest = builder.url(newHttpUrl).build();
                return chain.proceed(newRequest);

            } else {
                return chain.proceed(request);
            }
        }
    }

    /**
     * 输出请求响应日志信息
     */
    class OutLogInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Request.Builder requestBuilder = request.newBuilder();
            //添加请求头
            Request signedRequest = requestBuilder
//                        .addHeader("NBlock", "1")
//                        .addHeader("sessionToken", "")
//                        .addHeader("q_version", AppUtils.getVerCode(baseApplication) + "")
//                        .addHeader("device_id", "android-1001")
//                        .addHeader("device_os", "android")
//                        .addHeader("device_osversion", AppUtils.getSDKVersion() + "")
//                        .addHeader("app_name", "Nearby")
//                        .addHeader("sign", "")
//                        .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                        .addHeader("uuid", PreferencesUtils.getString(baseApplication, "uuid", "0"))
                    .build();
            long t1 = System.nanoTime();
            Response response = chain.proceed(signedRequest);
            //别问为什么 因为日志打印的数据 body会丢失 所有这里处理判断url进行额外处理下载
            if (signedRequest.url().toString().contains(".apk")) {
                Logger.d("进入下载 ...");
                return response
                        .newBuilder()
                        .body(new ProgressResponseBody2(response))
                        .build();
            }


            long t2 = System.nanoTime();
            MediaType contentType = null;
            String bodyString = null;
            if (response.body() != null) {
                contentType = response.body().contentType();
                bodyString = response.body().string();
            }
            double time = (t2 - t1) / 1e6d;

            switch (request.method()) {
                case "GET":
                    Logger.d(String.format(
                            "GET" + F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITH_BODY,
                            signedRequest.url(),
                            time,
                            signedRequest.headers(),
                            response.code(),
                            response.headers(),
                            stringifyResponseBody(bodyString)));
                    break;
                case "POST":
                    Logger.d(String.format(
                            "POST" + F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY,
                            signedRequest.url(),
                            time,
                            signedRequest.headers(),
                            stringifyRequestBody(signedRequest),
                            response.code(),
                            response.headers(),
                            stringifyResponseBody(bodyString)));
                    break;
                case "PUT":
                    Logger.d(String.format(
                            "POST" + F_REQUEST_WITH_BODY + F_RESPONSE_WITH_BODY,
                            signedRequest.url(),
                            time,
                            signedRequest.headers(),
                            signedRequest.body().toString(),
                            response.code(),
                            response.headers(),
                            stringifyResponseBody(bodyString)));
                    break;
                case "DELETE":
                    Logger.d(
                            String.format("DELETE " + F_REQUEST_WITHOUT_BODY + F_RESPONSE_WITHOUT_BODY,
                                    signedRequest.url(),
                                    time,
                                    signedRequest.headers(),
                                    response.code(),
                                    response.headers()));
                    break;
            }
            if (response.body() != null) {
                // 深坑！
                // 打印body后原ResponseBody会被清空，需要重新设置body
                ResponseBody body = ResponseBody.create(contentType, bodyString);
                return response.newBuilder().body(body).build();
            } else {
                return response;
            }

        }
    }

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * Cookie设置
     */
    public class CookieClass implements CookieJar {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    }



    public class SaveCookiesInterceptor implements Interceptor {

        private static final String COOKIE_PREF = "cookies_prefs";
        private Context mContext;

        protected SaveCookiesInterceptor(Context context) {
            mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            //set-cookie可能为多个
            if (!response.headers("set-cookie").isEmpty()) {
                List<String> cookies = response.headers("set-cookie");
                String cookie = encodeCookie(cookies);
                saveCookie(request.url().toString(), request.url().host(), cookie);
            }

            return response;
        }

        /**
         * 整合cookie为唯一字符串
         */
        private String encodeCookie(List<String> cookies) {
            StringBuilder sb = new StringBuilder();
            Set<String> set = new HashSet<>();
            for (String cookie : cookies) {
                String[] arr = cookie.split(";");
                for (String s : arr) {
                    if (set.contains(s)) {
                        continue;
                    }
                    set.add(s);

                }
            }

            for (String cookie : set) {
                sb.append(cookie).append(";");
            }

            int last = sb.lastIndexOf(";");
            if (sb.length() - 1 == last) {
                sb.deleteCharAt(last);
            }

            return sb.toString();
        }

        /**
         * 保存cookie到本地，这里我们分别为该url和host设置相同的cookie，其中host可选
         * 这样能使得该cookie的应用范围更广
         */
        private void saveCookie(String url, String domain, String cookies) {
            SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            if (TextUtils.isEmpty(url)) {
                throw new NullPointerException("url is null.");
            } else {
                editor.putString(url, cookies);
            }

            if (!TextUtils.isEmpty(domain)) {
                editor.putString(domain, cookies);
            }
            editor.apply();
        }

        /**
         * 清除本地Cookie
         *
         * @param context Context
         */
        public  void clearCookie(Context context) {
            SharedPreferences sp = context.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
            sp.edit().clear().apply();
        }
    }

    public class AddCookiesInterceptor implements Interceptor {
        private static final String COOKIE_PREF = "cookies_prefs";
        private Context mContext;

        public AddCookiesInterceptor(Context context) {
            mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            String cookie = getCookie(request.url().toString(), request.url().host());
            if (!TextUtils.isEmpty(cookie)) {
                builder.addHeader("Cookie", cookie);
            }

            return chain.proceed(builder.build());
        }

        private String getCookie(String url, String domain) {
            SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
            if (!TextUtils.isEmpty(url) && sp.contains(url) && !TextUtils.isEmpty(sp.getString(url, ""))) {
                return sp.getString(url, "");
            }
            if (!TextUtils.isEmpty(domain) && sp.contains(domain) && !TextUtils.isEmpty(sp.getString(domain, ""))) {
                return sp.getString(domain, "");
            }

            return null;
        }
    }

    @Provides
    @Singleton
    protected Retrofit provideRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        //拦截请求调试 我们打开Chrome，在地址栏输入chrome://inspect/
        builder.addNetworkInterceptor(new StethoInterceptor());
        //日志打印
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //拦截实现多个baseUrl切换问题
        builder.addInterceptor(new MoreBaseUrlInterceptor());
        //拦截请求输出内容
        builder.addInterceptor(new OutLogInterceptor());
        //Cookie
        builder.cookieJar(new CookieClass());
        //设置出现错误重链
        builder.retryOnConnectionFailure(true);
        //连接超时
        builder.connectTimeout(5, TimeUnit.SECONDS);
        //写入超时
        builder.writeTimeout(10, TimeUnit.SECONDS);
        //读取超时
        builder.readTimeout(10, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = builder.build();
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(HttpApi.BASE_URL)
                .build();
    }

}
