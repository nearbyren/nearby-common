package ren.nearby.common.di;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import ren.nearby.common_module.http.HttpModule;


/**
 * Created by Administrator on 2017/9/6 0006.
 * Singleton 为单例模式 因为所关联的HttpModule.class 包含了Singleton所以此处类也需要@Singleton
 */
@Singleton
@Component(
        modules = {
                //全局的Module,要确保提供的对象是全局唯一的
                HttpModule.class,
                //减少模版代码,需要依赖注入的只需要添加两行代码就好了
                AllActivitysModule.class,
                //在应用程序的AppDaggerComponent（application 中inject了）中，注入AndroidInjectionModule，
//                AndroidInjectionModule.class,
                // 以确保Android的类(Activity、Fragment、Service、BroadcastReceiver及ContentProvider等)可以绑定。
                // 一般把AndroidInjectionModule放在ApplicationComponent中，其他的Component依赖Application即可
                //使用的Fragment 是V4 包中的？不然就只需要AndroidInjectionModule
                AndroidSupportInjectionModule.class
        })
public interface DiComponent {
    //说明将HttpApi开放给其他Component使用
//    HttpApi providerHttpApi();

//    Context providerContext();


    //集成模式显示
    void inject(DiBaseApplication baseApplication);


}

