package ren.nearby.common_module

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger




open abstract class BaseActivityTitleKot<T : KotIPresenter?> : AppCompatActivity() {
    /**
     * 是否需要加载数据失败预览
     *
     * @return
     */
    open fun isStateLayout(): Boolean {
        return false
    }


    override fun onStart() {
        super.onStart()
        //获取子类实现的操作层 管理生命周期
        mPresenter = getPresenterKot()
        Logger.d(if (mPresenter == null) "mPresenter onStart - 1 " else " mPresenter onStart - 0 ")
    }

    /**
     * 管理操作数据层的生命周期
     */
    override fun onDestroy() {
        super.onDestroy()
        Logger.d(if (mPresenter == null) "mPresenter onDestroy - 1 " else "mPresenter onDestroy - 0 ")
        if (mPresenter != null) {
            mPresenter!!.detachView()
        }
    }

    /**
     * 操作数据层
     */
    protected var mPresenter: T? = null

    /**
     * 抽象由子类实现
     *
     * @return
     */
    abstract fun getPresenterKot(): T

    /**
     * 加载布局文件
     *
     * @return
     */
    abstract fun getLayoutResKot(): Int

    /**
     * 根布局 [可控制网络加载隐藏的部分]
     *
     * @return
     */
    abstract fun getLayoutParentKot(): ViewGroup?

    /**
     * 内容布局[可控制网络加载隐藏的部分]
     *
     * @return
     */
    abstract fun getLayoutContentKot(): View?

    /**
     * 初始化控件布局绑定
     */
    abstract fun initKnife()

    /**
     * 初始化控件
     */
    abstract fun initView()

    /**
     * 当前activity
     *
     * @return
     */
    abstract fun getActivityKot(): BaseActivityKot?
}
