package ren.nearby.common.di;


import dagger.Module;
import ren.nearby.common_module.scope.BaseActivityComponentKt;


/**
 * 配置所有activity注入
 * Created by Administrator on 2018/5/3 0003.
 */


@Module(subcomponents = {
        // 注入 activity
        BaseActivityComponentKt.class,
})
public abstract class AllActivitysModule {



}
