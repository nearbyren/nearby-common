package ren.nearby.common_module.scope;

import javax.inject.Singleton;

import dagger.Component;
import ren.nearby.common_module.http.HttpApi;
import ren.nearby.common_module.http.HttpModule;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/9/6 0006.
 */
@Singleton
@Component(modules = {HttpModule.class})
public interface AppComponent {

    HttpApi getRestApi();

    Retrofit getRetrofit();
}
