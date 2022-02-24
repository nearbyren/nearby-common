package ren.nearby.common_module

import android.app.Fragment
import android.content.Context
import dagger.android.AndroidInjection

abstract class BaseLazyLoadFragmentTitleKot<T : KotIPresenter?> : Fragment() {
    override fun onAttach(context: Context) {
//        AndroidInjection.inject(this)
//        AndroidInjection.inject(this)
        super.onAttach(context)
    }

    /**
     * 操作层数据
     */
    protected var mPresenter: T? = null

    /**
     * 泛型注入操作层
     *
     * @param mPresenter
     */
    fun setPresenter(mPresenter: T) {
        this.mPresenter = mPresenter
    }
}
