package nearby.composing.builds

object BuildPlugVersion {
    const val mainVersion = 1
    const val mainVersionName = "1.0.0"

    const val libBaseVersion = 1
    const val libBaseVersionName = "1.0.0"

    const val libStyleVersion = 1
    const val libStyleVersionName = "1.0.0"

    const val libUtilVersion = 1
    const val libUtilVersionName = "1.0.0"
}

/**
 * 配置和 build相关的
 */
object BuildVersion {
    //时间：2017.2.13；每次修改版本号都要添加修改时间
    //V1_1_2_161209_beta
    //V主版本号_子版本号_阶段版本号_日期版本号_希腊字母版本号
    //程序编译app时候用的sdk版本 建议最新
    const val compileSdkVersion = 29

    //规则是可以用高的构建工具来构建低版本Sdk的工程
    const val buildToolsVersion = "29.0.2"

    //程序运行的最低的要求的Sdk
    const val minSdkVersion = 19

    //[使用cameraX时记得切换成这个]
//    const val minSdkVersion = 21
    //向前兼容的作用
    const val targetSdkVersion = 29
    const val versionCode = 1
    const val versionName = "v1.0.0"

    //热更新配置
    const val resguardXXX = "resguard-0428-15-19-56"

    const val arouter_compiler_version = "1.2.2"
    const val arouter_api_version = "1.5.0"
    const val multidex_version = "2.0.1"

    //加载图片版本
    const val glide_version = "4.11.0"

    //日志工具版本
    const val logger_version = "2.1.1"
    const val timber_version = "4.7.1"

    //响应式编程库
    const val rxjava_version = "2.1.3"
    const val rxjava_android_version = "2.0.1"

    //REST客户端库 RESTful架构
    const val retrofit_version = "2.3.0"
    const val retrofit_converter_gson_version = "2.3.0"
    const val rx_binding_version = "2.1.1"

    //注解注入
//            https://www.cnblogs.com/woshimrf/p/hello-dagger.html
//            https://blog.csdn.net/mq2553299/article/details/77725912
    const val dagger_version = "2.16"
    const val dagger_compiler_version = "2.16"
    const val dagger_android_version = "2.16"

    //网络请求库
    const val okhttp_version = "3.8.1"

    //拦截请求调试
    const val stetho_version = "1.5.0"

    //bugly崩溃日志
    const val crashreport_version = "3.0.1"
    const val nativecrashreport_version = "3.7.1"

    //新布局
    const val constraint_layout_version = "1.1.2"
    const val crashwoodpecker_version = "2.1.1"
    const val acraVersion = "5.1.3"

    //内存检测
    const val leakcanary_version = "1.6.2"

    //热更新
    const val TINKER_PATCH = "1.9.14.16"

    //广告轮播图版本
    const val banner_version = "1.4.9"

    //导航切换
    const val smarttablayout_version = "1.6.1@aar"

    //美团打包
    const val walle_version = "1.1.6"
    const val kotlin_version = "1.1.2-4"
    const val MaterialIntroView_version = "1.6.0"
    const val slidingtutorial_version = "0.9"
    const val Toasty_version = "1.2.8"
    const val light_version = "1.0.0"
    const val stateframelayout_version = "2.0.2"
    const val sdk_panowidget_version = "1.80.0"
    const val permissionsdispatcher_version = "2.1.3"

    const val crashreport_upgrade_version = "1.3.4"

    //基础控件依赖的版本
    const val support_library_version = "1.0.0"
    const val nav_fra_ktx_version = "2.0.0"
    const val kot_cor_and_version = "1.3.1"
    const val lcy_ext_version = "2.2.0"
    const val dbing_compiler_version = "3.4.0"
    const val camerax_version = "1.0.0-beta06"
    const val camerax_version_view = "1.0.0-alpha13"
}


/**
 * 项目相关配置
 */
object BuildConfig {
    //AndroidX
    const val appcompat = "androidx.appcompat:appcompat:1.2.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val coreKtx = "androidx.core:core-ktx:1.3.2"
    const val material = "com.google.android.material:material:1.2.1"
    const val junittest = "androidx.test.ext:junit:1.1.2"
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"
    const val cardview = "androidx.cardview:cardview:1.0.0"

    //Depend
    const val junit = "junit:junit:4.12"
    const val espresso_core = "com.android.support.test.espresso:espresso-core:3.0.2"
    const val guava = "com.google.guava:guava:24.1-jre"
    const val commons = "org.apache.commons:commons-lang3:3.6"
    const val zxing = "com.google.zxing:core:3.3.2"

    //leakcanary
//    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.4"

    //jetPack
    const val room_runtime = "androidx.room:room-runtime:2.2.5"
    const val room_compiler = "androidx.room:room-compiler:2.2.5"
    const val room_rxjava2 = "androidx.room:room-rxjava2:2.2.5"
    const val lifecycle_extensions = "android.arch.lifecycle:extensions:1.1.1"
    const val lifecycle_extensions2 = "androidx.lifecycle:lifecycle-extensions:2.2.0"

    const val lifecycle_compiler = "android.arch.lifecycle:compiler:1.1.1"
    const val rxlifecycle = "com.trello.rxlifecycle3:rxlifecycle:3.1.0"
    const val rxlifecycle_components = "com.trello.rxlifecycle3:rxlifecycle-components:3.1.0"



    const val multidex = "androidx.multidex:multidex:2.0.1"
    const val camera_core = "androidx.camera:camera-core:1.0.0-beta06"
    const val camera_camera2 = "androidx.camera:camera-camera2:1.0.0-beta06"
    const val camera_lifecycle = "androidx.camera:camera-lifecycle:1.0.0-beta06"
    const val camera_view = "androidx.camera:camera-view:1.0.0-alpha13"
    const val camera_extensions = "androidx.camera:camera-extensions:1.0.0-alpha13"

    const val dbing_compiler = "androidx.databinding:databinding-compiler:3.4.0"
    const val lcy_ext = "androidx.lifecycle:lifecycle-extensions:2.2.0"

    //Android Jetpack 学习
    const val nav_fra_ktx = "androidx.navigation:navigation-fragment-ktx:2.0.0"

    //Kotlin
     const val nav_ui_ktx = "androidx.navigation:navigation-ui-ktx:2.0.0"
    const val kot_cor_and = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7"
    const val kot_cor_cor = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7"

    const val arouter_api = "com.alibaba:arouter-api:1.5.2"
    const val arouter_compiler = "com.alibaba:arouter-compiler:1.5.2"


    const val tinker_lib = "com.tencent.tinker:tinker-android-lib:1.9.14.18"
    const val tinker_loader = "com.tencent.tinker:tinker-android-loader:1.9.14.18"
    const val tinker_ano = "com.tencent.tinker:tinker-android-anno:1.9.14.18"
    const val tinker_ano_support = "com.tencent.tinker:tinker-android-anno-support:1.9.14.18"


    const val leakcanary_a = "com.squareup.leakcanary:leakcanary-android:1.6.2"
    const val leakcanary_a_n_o = "com.squareup.leakcanary:leakcanary-android-no-op:1.6.2"
    const val leakcanary_s_f = "com.squareup.leakcanary:leakcanary-support-fragment:1.6.2"

    const val crashreport_upgrade = "com.tencent.bugly:crashreport_upgrade:1.3.4"
    const val glide = "com.github.bumptech.glide:glide:4.11.0"

    //日志工具类
    const val logger = "com.orhanobut:logger:2.1.1"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val rxjava = "io.reactivex.rxjava2:rxjava:2.1.3"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.0.1"
    const val rxbinding2 = "com.jakewharton.rxbinding2:rxbinding:2.1.1"

    //注解框架依赖
    const val dagger2 = "com.google.dagger:dagger:2.16"
    const val dagger2_compiler = "com.google.dagger:dagger-compiler:2.19"
    const val dagger2_android = "com.google.dagger:dagger-android:2.19"
    const val dagger2_support = "com.google.dagger:dagger-android-support:2.16"
    const val dagger2_processor = "com.google.dagger:dagger-android-processor:2.16"

    //okhttp3
    const val okhttp = "com.squareup.okhttp3:okhttp:3.8.1"
    const val logging_interceptor = "com.squareup.okhttp3:logging-interceptor:3.8.1"
    const val retrofit = "com.squareup.retrofit2:retrofit:2.3.0"
    const val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:2.3.0"
    const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:2.3.0"
    const val converter_calars = "com.squareup.retrofit2:converter-scalars:2.3.0"

    //拦截请求调试
    const val stetho_okhttp3 = "com.facebook.stetho:stetho-okhttp3:1.5.0"

    //拦截请求调试
    const val stetho = "com.facebook.stetho:stetho:1.5.0"

    const val crashwoodpecker = "me.drakeet.library:crashwoodpecker:2.1.1"

    //广告栏
    const val banner = "com.youth.banner:banner:1.4.9"
    const val smarttablayout_library = "com.ogaclejapan.smarttablayout:library:1.6.1@aar"
    const val smarttablayout_utils_v4 = "com.ogaclejapan.smarttablayout:utils-v4:1.6.1@aar"

    //美团打包
    const val mtwalle = "com.meituan.android.walle:library:1.1.6"
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:1.1.2-4"

    //功能引导指示图
    const val MaterialIntroView = "com.github.iammert:MaterialIntroView:1.6.0"

    //引导图
    const val slidingtutorial = "com.cleveroad:slidingtutorial:0.9"

    //toast
    const val Toasty = "com.github.GrenderG:Toasty:1.2.8"
    const val light = "io.github.tonnyl:light:1.0.0"
    const val stateframelayout = "am.widget:stateframelayout:2.0.2"
    const val sdk_panowidget = "com.google.vr:sdk-panowidget:1.80.0"

    const val permissionsdispatcher = "com.github.hotchemi:permissionsdispatcher:2.1.3"
    const val permissionsdispatcher_processor = "com.github.hotchemi:permissionsdispatcher-processor:2.1.3"


    //新布局 Android Support
    const val constraint_layout = "androidx.constraintlayout:constraintlayout:1.1.2"
    const val constraint_layoutSolver = "androidx.constraintlayout:constraintlayout-solver:1.1.2"

    //bugly崩溃
    const val crashreport = "com.tencent.bugly:crashreport:3.3.92"
    const val nativecrashreport = "com.tencent.bugly:nativecrashreport:3.9.0"

    //内存泄漏
    const val leakcanary =  "com.squareup.leakcanary:leakcanary-android:2.7"

    //这个包是为了考虑API level 7(即Android 2.1)及以上版本而设计的，
    // 但是v7是要依赖v4这个包的，v7支持了Action Bar以及一些Theme的兼容。
    //百分比布局
    const val percent = "androidx.percentlayout:percentlayout:1.0.0"
    const val appcompat_v7 = "androidx.appcompat:appcompat:1.0.0"
    const val cardview_v7 = "androidx.cardview:cardview:1.0.0"
    const val design = "com.google.android.material:material:1.0.0"
    const val support_compat = "androidx.core:core:1.0.0"
    const val support_v13 = "androidx.legacy:legacy-support-v13:1.0.0"
    const val support_annotations = "androidx.annotation:annotation:1.0.0"
}