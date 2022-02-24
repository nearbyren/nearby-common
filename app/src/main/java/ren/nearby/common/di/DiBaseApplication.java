package ren.nearby.common.di;

import android.app.Activity;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by Administrator on 2018/5/7 0007.
 */
public class DiBaseApplication extends TinkerApplication implements HasActivityInjector, /*HasFragmentInjector, */HasSupportFragmentInjector {

    public DiBaseApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL,"ren.nearby.common.di.BaseApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader",false);
    }
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

//    @Inject
//    DispatchingAndroidInjector<Fragment> fragmentInjector;
//
//    @Override
//    public AndroidInjector<Fragment> fragmentInjector() {
//        return fragmentInjector;
//    }

    @Inject
    DispatchingAndroidInjector<androidx.fragment.app.Fragment> supportFragmentInjector;

    @Override
    public AndroidInjector<androidx.fragment.app.Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }
}
