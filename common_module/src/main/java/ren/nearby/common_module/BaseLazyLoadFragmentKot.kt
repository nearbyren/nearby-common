package ren.nearby.common_module

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orhanobut.logger.Logger
import dagger.android.support.AndroidSupportInjection


abstract class BaseLazyLoadFragmentKot<T : KotIPresenter?> : Fragment(),
    ToolBarHelperKot.OnBackListener {
    //https://www.cnblogs.com/fajieyefu/p/6092465.html 生命周期
    //setUserVisibleHint()->onAttach()->onCreate()->onCreateView()->onActivityCreated()->onStart()->onResume()；
    protected var toolBarBuilder: ToolBarHelperKot.Builder? = null
    protected fun initViewHeader(view: View?) {
        toolBarBuilder = ToolBarHelperKot.Builder(activity as BaseActivityKot)
        toolBarBuilder!!.setView(view)
        toolBarBuilder!!.setOnBackListener(activity as BaseActivityKot)
    }
    protected var rootView: View? = null
    private var mIsFirstVisible = true
    private var isViewCreated = false

    /**
     * 20180818
     *
     * @return
     */
    private var isSupportVisible = false

    /**
     * 获取类名
     *
     * @return
     */
    protected val name: String
        protected get() = javaClass.getSimpleName()

    override fun onAttach(context: Context) {
        //初始化dagger2
//        AndroidSupportInjection.inject(this)
        Logger.d("$name->onAttach = $context")
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if (rootView == null) {
            rootView = inflater.inflate(layoutRes, container, false)
        }
        initViewHeader(rootView)
        initView(rootView)
        return rootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //走这里分发可见状态情况有两种，1. 已缓存的 Fragment 被展示的时候 2. 当前 Fragment 由可见变成不可见的状态时
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见，
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立，可见状态在 onActivityCreated 中分发
        // 对于非默认 tab，View 创建完成  isViewCreated =  true 成立，走这里分发可见状态，mIsFirstVisible 此时还为 false  所以第一次可见状态也将通过这里分发
        if (isViewCreated) {
            if (isVisibleToUser && !isSupportVisible) {
                dispatchUserVisibleHint(true)
            } else if (!isVisibleToUser && isSupportVisible) {
                dispatchUserVisibleHint(false)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 将 View 创建完成标志位设为 true
        isViewCreated = true
        // 默认 Tab getUserVisibleHint() = true !isHidden() = true
        // 对于非默认 tab 或者非默认显示的 Fragment 在该生命周期中只做了 isViewCreated 标志位设置 可见状态将不会在这里分发
        if (!isHidden && userVisibleHint) {
            dispatchUserVisibleHint(true)
        }
    }

    /**
     * 该方法与 setUserVisibleHint 对应，调用时机是 show，hide 控制 Fragment 隐藏的时候，
     * 注意的是，只有当 Fragment 被创建后再次隐藏显示的时候才会调用，第一次 show 的时候是不会回调的。
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            dispatchUserVisibleHint(false)
        } else {
            dispatchUserVisibleHint(true)
        }
    }

    /**
     * 需要再 onResume 中通知用户可见状态的情况是在当前页面再次可见的状态 !mIsFirstVisible 可以保证这一点，
     * 而当前页面 Activity 可见时所有缓存的 Fragment 都会回调 onResume
     * 所以我们需要区分那个Fragment 位于可见状态
     * (!isHidden() && !currentVisibleState && getUserVisibleHint()）可条件可以判定哪个 Fragment 位于可见状态
     */
    override fun onResume() {
        super.onResume()
        if (!mIsFirstVisible) {
            if (!isHidden && !isSupportVisible && userVisibleHint) {
                dispatchUserVisibleHint(true)
            }
        }
    }

    /**
     * 当用户进入其他界面的时候所有的缓存的 Fragment 都会 onPause
     * 但是我们想要知道只是当前可见的的 Fragment 不可见状态，
     * currentVisibleState && getUserVisibleHint() 能够限定是当前可见的 Fragment
     */
    override fun onPause() {
        super.onPause()
        if (isSupportVisible && userVisibleHint) {
            dispatchUserVisibleHint(false)
        }
    }

    /**
     * 统一处理 显示隐藏  做两件事
     * 设置当前 Fragment 可见状态 负责在对应的状态调用第一次可见和可见状态，不可见状态函数
     *
     * @param visible
     */
    private fun dispatchUserVisibleHint(visible: Boolean) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
//        LogUtils.e(getClass().getSimpleName() + "  dispatchUserVisibleHint isParentInvisible() " + isParentInvisible());
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible) return
        //
//        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
//        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (isSupportVisible == visible) {
            return
        }
        isSupportVisible = visible
        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false
                onFragmentFirstVisible()
            }
            onFragmentResume()
            dispatchChildVisibleState(true) //20180818
        } else {
            dispatchChildVisibleState(false) //20180818
            onFragmentPause()
        }
    }

    /**
     * 20180818
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private val isParentInvisible: Boolean
        private get() {
            val fragment = parentFragment as BaseLazyLoadFragmentKot<*>?
            return fragment != null && !fragment.isSupportVisible
        }

    /**
     * 20180818
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     *
     *
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     *
     *
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private fun dispatchChildVisibleState(visible: Boolean) {
        val childFragmentManager = childFragmentManager
        val fragments = childFragmentManager.fragments
        if (!fragments.isEmpty()) {
            for (child in fragments) {
                if (child is BaseLazyLoadFragmentKot<*> && !child.isHidden() && child.getUserVisibleHint()) {
                    child.dispatchUserVisibleHint(visible)
                }
            }
        }
    }

    /**
     * 20180818
     *
     * @param fragment
     * @return
     */
    private fun isFragmentVisible(fragment: Fragment): Boolean {
        return !fragment.isHidden && fragment.userVisibleHint
    }

    /**
     * 对用户第一次可见
     */
    open fun onFragmentFirstVisible() {
        Logger.d(javaClass.getSimpleName() + "  ")
    }

    /**
     * 对用户可见
     */
    open fun onFragmentResume() {
        Logger.d(javaClass.getSimpleName() + "  对用户可见")
    }

    /**
     * 对用户不可见
     */
    open fun onFragmentPause() {
        Logger.d(javaClass.getSimpleName() + "  对用户不可见")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //当 View 被销毁的时候我们需要重新设置 isViewCreated mIsFirstVisible 的状态
        Logger.d("$name-> onDestroyView")
        if (mPresenter != null) {
            mPresenter!!.detachView()
        }
        isViewCreated = false
        mIsFirstVisible = true
    }

    override fun onBackClick() {}

    /**
     * 返回布局 resId
     *
     * @return layoutId
     */
    protected abstract val layoutRes: Int

    /**
     * 初始化view
     *
     * @param rootView
     */
    protected abstract fun initView(rootView: View?)
    protected abstract val fragment: BaseLazyLoadFragmentKot<*>?

    /**
     * 操作层数据
     */
    protected var mPresenter: T? = null
    /**
     * 抽象 由子类实现注入操作
     *
     * @return
     */
    /**
     * 泛型注入操作层
     *
     * @param mPresenter
     */
    open fun setPresenter(mPresenter: T) {
        this.mPresenter = mPresenter
    }
}
