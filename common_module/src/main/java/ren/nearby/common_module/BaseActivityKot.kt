package ren.nearby.common_module

import android.annotation.TargetApi
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.Visibility
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.alibaba.android.arouter.launcher.ARouter
import com.orhanobut.logger.Logger
import dagger.android.AndroidInjection
import ren.nearby.common_module.PermissionPageUtils
import ren.nearby.common_module.R
import ren.nearby.common_module.back.SwipeBackLayout
import ren.nearby.common_module.empty.PageStateLayout


open class BaseActivityKot :
    BaseActivityTitleKot<KotIPresenter?>(), ToolBarHelperKot.OnBackListener, ToolBarHelperKot2.OnBackListener {
    open var toolBarBuilder: ToolBarHelperKot.Builder? = null
    open var toolBarBuilder2: ToolBarHelperKot2.Builder? = null
    /**
     * 控件注解绑定
     */
    //    private Unbinder unbinder;
    /**
     * 侧滑关闭
     */
    private var swipeBackLayout: SwipeBackLayout? = null

    /**
     * 透明侧滑背景层
     */
    private var ivShadow: ImageView? = null

    /**
     * 预览 空数据 异常
     */
    var pageStateLayout: PageStateLayout? = null
    private val type = 0

    //处理网络层回调Activity操作
    interface NetworkRequestCallback {
        fun universal(type: Int, o: Any?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        AndroidInjection.inject(this)
        //阿里路由注册获取参数
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        //activity传递数据Intent
        onIntent()
        pageStateLayout = PageStateLayout(this)
        setContentView(getLayoutResKot())

        //操作标题栏
        initViewHeader()
        initKnife()
        initView()
    }

    override fun onStart() {
        super.onStart()
        Logger.d("onStart")
    }

    override fun onResume() {
        super.onResume()
        Logger.d("onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("onDestroy")
        //        unbinder.unbind();
    }

    override fun initView() {
        Logger.d("initView")
    }

    /**
     * 获取传递数据
     */
    open fun onIntent() {
        Logger.d("onIntent")
    }

    protected fun initViewHeader() {
        if (toolBarBuilder == null) {
            toolBarBuilder = ToolBarHelperKot.Builder(this)
            toolBarBuilder!!.setOnBackListener(this)
        }
        if (toolBarBuilder2 == null) {
            toolBarBuilder2 = ToolBarHelperKot2.Builder(this)
            toolBarBuilder2!!.setOnBackListener(this)
        }
    }

    override fun onBackClick() {
        finish()
    }

    /**
     * 处理侧滑层
     *
     * @return
     */
    private val container: View
        private get() {
            val container = RelativeLayout(this)
            swipeBackLayout = SwipeBackLayout(this)
            swipeBackLayout!!.setDragEdge(SwipeBackLayout.DragEdge.LEFT)
            ivShadow = ImageView(this)
            ivShadow!!.setBackgroundColor(resources.getColor(R.color.theme_black_7f))
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
            container.addView(ivShadow, params)
            container.addView(swipeBackLayout)
            swipeBackLayout!!.setOnSwipeBackListener { fa, fs -> ivShadow!!.alpha = 1 - fs }
            return container
        }

    override fun getResources(): Resources {
        var resources = super.getResources()
        val newConfig = resources.configuration
        val displayMetrics = resources.displayMetrics
        if (resources != null && newConfig.fontScale != 1f) {
            newConfig.fontScale = 1f
            if (Build.VERSION.SDK_INT >= 17) {
                val configurationContext = createConfigurationContext(newConfig)
                resources = configurationContext.resources
                displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale
            } else {
                resources.updateConfiguration(newConfig, displayMetrics)
            }
        }
        return resources
    }

    /**
     * 处理那些activity可以侧滑
     *
     * @param layoutResID
     */
    override fun setContentView(layoutResID: Int) {
        if (isStateLayout()) {
            Logger.d(" setContentView - if ")
            super.setContentView(container)
            val view = LayoutInflater.from(this).inflate(layoutResID, null)
            view.setBackgroundColor(resources.getColor(R.color.window_background))
            swipeBackLayout!!.addView(view)
        } else if (layoutResID != 0) {
            Logger.d(" setContentView - else ")
            super.setContentView(layoutResID)
        }
    }


    override fun getPresenterKot(): KotIPresenter? {
//        TODO("Not yet implemented")
        return null
    }

    override fun getLayoutResKot(): Int {
//        TODO("Not yet implemented")
        return 0
    }

    override fun getLayoutParentKot(): ViewGroup? {
//        TODO("Not yet implemented")
        return null
    }

    override fun getLayoutContentKot(): View? {
//        TODO("Not yet implemented")
        return null
    }

    override fun initKnife() {
//        Process: ren.nearby.jetpack, PID: 17680
//        kotlin.NotImplementedError: An operation is not implemented: Not yet implemented
//        TODO("Not yet implemented")
    }

    override fun getActivityKot(): BaseActivityKot? {
//        TODO("Not yet implemented")
        return null
    }

    /**
     * 动画
     *
     * @param i
     */
//    fun transitionTo(i: Intent?) {
//        val pairs = TransitionHelper.createSafeTransitionParticipants(this, true)
//        val transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs)
//        startActivity(i, transitionActivityOptions.toBundle())
//    }

    /**
     * 进入界面动画效果
     */
    fun setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initWindowAnimations()
        }
    }

    @TargetApi(21)
    fun initWindowAnimations() {
        val transition: Transition
        transition = if (type == TYPE_PROGRAMMATICALLY) {
            buildEnterTransition()
        } else {
            TransitionInflater.from(this).inflateTransition(R.transition.slide_from_bottom)
        }
        window.enterTransition = transition
    }

    /**
     * 代码动画方式
     *
     * @return
     */
    @TargetApi(21)
    fun buildEnterTransition(): Visibility {
        val enterTransition = Slide()
        enterTransition.duration = 100L
        enterTransition.slideEdge = Gravity.RIGHT
        return enterTransition
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionPageUtils.REQUEST_PERMISSION_CODE) {
            Logger.d("请求权限接收回调....")
        }
    }

    override fun finish() {
        super.finish()
        Logger.d("finish 退出")
//        overridePendingTransitionExit()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        Logger.d("start 进入")
//        overridePendingTransitionEnter()
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
//    protected fun overridePendingTransitionEnter() {
//        overridePendingTransition(R.anim.slide_form_right, R.anim.slide_to_left)
//    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
//    protected fun overridePendingTransitionExit() {
//        overridePendingTransition(R.anim.slide_form_left, R.anim.slide_to_right)
//    }

    companion object {
        const val TYPE_PROGRAMMATICALLY = 0
    }


}
