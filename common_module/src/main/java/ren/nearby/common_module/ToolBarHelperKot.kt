package ren.nearby.common_module

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger
import ren.nearby.common_module.R
import ren.nearby.common_module.empty.PageStateLayout
import ren.nearby.common_module.setbar.StatusBarUtil3
import ren.nearby.common_module.setbar.StatusBarUtils
import ren.nearby.common_module.setbar.StatusBarUtils2


class ToolBarHelperKot internal constructor(
    activity: BaseActivityKot?,
    onBackListener: OnBackListener?,
    onNextListener: OnNextListener?,
    onEmptyListener: OnEmptyListener?,
    onErrorListener: OnErrorListener?,
    view: View?,
    title: String?,
    titleColor: Int,
    backgroundColor: Int,
    titlePosition: Int,
    iconLeft: Int,
    isBack: Boolean,
    iconRight: Int,
    fontTypeStr: String?,
    rightText: String?,
    rightTextColor: Int,
    layout: Int,
    parent: ViewGroup?,
    content: View?,
    pageStateLayout: PageStateLayout?,
    statusBarType: Int
) {
    interface OnEmptyListener {
        fun onEmptyClick()
    }

    interface OnErrorListener {
        fun onOnErrorClick()
    }

    interface OnBackListener {
        fun onBackClick()
    }

    interface OnNextListener {
        fun onNextClick()
    }

    //初始化基本数据
    class Builder(private val activity: BaseActivityKot) {
        var onBackListener: OnBackListener? = null
        var onNextListener: OnNextListener? = null
        var onEmptyListener: OnEmptyListener? = null
        var onErrorListener: OnErrorListener? = null
        private var view: View? = null
        private var title: String? = null
        private var titleColor = 0
        private var backgroundColor = 0
        private var titlePosition = 1
        private var fontTypeStr: String? = null
        private var iconLeft = 0
        private var isBack = false
        private var iconRight = 0
        private var rightText: String? = null
        private var rightTextColor = 0
        private var statusBarTintResource = 0
        private var layout = 0
        private var parent: ViewGroup? = null
        private var content: View? = null
        private var pageStateLayout: PageStateLayout? = null
        private var statusBarType = 0
        fun onLoading(): Builder {
            Logger.d(if (pageStateLayout == null) "pageStateLayout - 1 " else "pageStateLayout - 2")
            if (pageStateLayout != null) {
                pageStateLayout!!.onLoading()
            }
            return this
        }

        fun onRequesting(): Builder {
            if (pageStateLayout != null) {
                pageStateLayout!!.onRequesting()
            }
            return this
        }

        fun onEmpty(): Builder {
            if (pageStateLayout != null) {
                pageStateLayout!!.onEmpty()
            }
            return this
        }

        fun onError(): Builder {
            if (pageStateLayout != null) {
                pageStateLayout!!.onError()
            }
            return this
        }

        fun onSucceed(): Builder {
            if (pageStateLayout != null) {
                pageStateLayout!!.onSucceed()
            }
            return this
        }

        fun setPageStateLayout(
            parent: ViewGroup?,
            content: View?,
            pageStateLayout: PageStateLayout?
        ): Builder {
            this.parent = parent
            this.content = content
            this.pageStateLayout = pageStateLayout
            return this
        }

        fun setPageStateLayout(layout: Int, pageStateLayout: PageStateLayout?): Builder {
            this.layout = layout
            this.pageStateLayout = pageStateLayout
            return this
        }

        fun setOnBackListener(onBackListener: BaseActivityKot): Builder {
            this.onBackListener = onBackListener
            return this
        }

        fun setOnNextListener(onNextListener: OnNextListener?): Builder {
            this.onNextListener = onNextListener
            return this
        }

        fun setonEmptyListener(onEmptyListener: OnEmptyListener?): Builder {
            this.onEmptyListener = onEmptyListener
            return this
        }

        fun setonErrorListener(onErrorListener: OnErrorListener?): Builder {
            this.onErrorListener = onErrorListener
            return this
        }

        fun setStatusBarType(statusBarType: Int): Builder {
            this.statusBarType = statusBarType
            return this
        }

        fun setView(view: View?): Builder {
            this.view = view
            return this
        }

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setTitleColor(titleColor: Int): Builder {
            this.titleColor = titleColor
            return this
        }

        fun setBackgroundColor(backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun setTitlePosition(titlePosition: Int): Builder {
            this.titlePosition = titlePosition
            return this
        }

        fun setFontTypeStr(fontTypeStr: String?): Builder {
            this.fontTypeStr = fontTypeStr
            return this
        }

        fun setIconLeft(iconLeft: Int): Builder {
            this.iconLeft = iconLeft
            return this
        }

        fun setBack(back: Boolean): Builder {
            isBack = back
            return this
        }

        fun setIconRight(iconRight: Int): Builder {
            this.iconRight = iconRight
            return this
        }

        fun setIsRightText(rightText: String?): Builder {
            this.rightText = rightText
            return this
        }

        fun setIsRightTextColor(rightTextColor: Int): Builder {
            this.rightTextColor = rightTextColor
            return this
        }

        fun setStatusBarTintResource(statusBarTintResource: Int): Builder {
            this.statusBarTintResource = statusBarTintResource
            StatusBarUtils.setStatusBar(activity, false, false, statusBarTintResource)
            StatusBarUtils2.setStatusTextColor(true, activity)
            return this
        }

        /**
         * 构建工具
         *
         * @return
         */
        fun build(): ToolBarHelperKot {
            return ToolBarHelperKot(
                activity,
                onBackListener,
                onNextListener,
                onEmptyListener,
                onErrorListener,
                view,
                title,
                titleColor,
                backgroundColor,
                titlePosition,
                iconLeft,
                isBack,
                iconRight,
                fontTypeStr,
                rightText,
                rightTextColor,
                layout,
                parent,
                content,
                pageStateLayout,
                statusBarType
            )
        }
    }

    /**
     * @param activity        Activity界面
     * @param onBackListener  是否开启左边点击事件
     * @param onNextListener  是否开启右边点击事件
     * @param view            Fragment
     * @param title           标题内容
     * @param titleColor      标题颜色
     * @param backgroundColor 标题栏背景色
     * @param titlePosition   标题显示的位置
     * @param iconLeft        是否更新左边按钮图标
     * @param isBack          是否开启左边点击事件
     * @param iconRight       是否更新右边按钮图标
     * @param fontTypeStr     标题字体类型
     * @param rightText       是否设置右边字体
     * @param rightTextColor  是否设置右边字体颜色
     */
    init {
        var backgroundColor = backgroundColor
        if (activity != null) {


            backgroundColor = if (backgroundColor == 0) R.color.colorPrimary else backgroundColor
            Logger.d("statusBarType = $statusBarType")
            when (statusBarType) {
                1 -> StatusBarUtil3.setColor(
                    activity,
                    activity!!.resources.getColor(backgroundColor)
                )
                2 -> {
                }
                3 -> StatusBarUtil3.setTransparent(activity)
                4 -> {
                }
                5 -> StatusBarUtil3.setTranslucentForImageViewInFragment(activity, 0, null)
            }
            if (view != null) {
                isView(
                    view,
                    activity,
                    onBackListener,
                    titleColor,
                    titlePosition,
                    title,
                    fontTypeStr,
                    isBack,
                    iconLeft,
                    iconRight,
                    rightText,
                    onNextListener,
                    rightTextColor,
                    backgroundColor
                )
            } else {
                isActivity(
                    layout,
                    activity,
                    pageStateLayout,
                    onEmptyListener,
                    onErrorListener,
                    parent,
                    content,
                    onBackListener,
                    titleColor,
                    titlePosition,
                    title,
                    fontTypeStr,
                    isBack,
                    iconLeft,
                    iconRight,
                    rightText,
                    onNextListener,
                    rightTextColor,
                    backgroundColor
                )
            }
        }
    }

    private fun isActivity(
        layout: Int,
        activity: BaseActivityKot,
        pageStateLayout: PageStateLayout?,
        onEmptyListener: OnEmptyListener?,
        onErrorListener: OnErrorListener?,
        parent: ViewGroup?,
        content: View?,
        onBackListener: OnBackListener?,
        titleColor: Int,
        titlePosition: Int,
        title: String?,
        fontTypeStr: String?,
        isBack: Boolean,
        iconLeft: Int,
        iconRight: Int,
        rightText: String?,
        onNextListener: OnNextListener?,
        rightTextColor: Int,
        backgroundColor: Int
    ) {
        //            if (layout != 0) {
        //                Logger.d("laile");
        //                View stateview = LayoutInflater.from(activity).inflate(layout, null);
        //                pageStateLayout.setOnEmptyListener(new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        onEmptyListener.onEmptyClick();
        //                    }
        //                }).setOnErrorListener(new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        onErrorListener.onOnErrorClick();
        //                    }
        //                }).load(activity, stateview);
        //            }
        if (layout != 0) { //网络加载错误隐藏全屏幕布局
            Logger.d("laile")
            val stateview = LayoutInflater.from(activity).inflate(layout, null)
            pageStateLayout!!.setOnEmptyListener { onEmptyListener!!.onEmptyClick() }
                .setOnErrorListener { onErrorListener!!.onOnErrorClick() }
                .load(activity!!, stateview)
        } else { //指定被隐藏的布局
            pageStateLayout?.setOnEmptyListener { onEmptyListener!!.onEmptyClick() }
                ?.setOnErrorListener { onErrorListener!!.onOnErrorClick() }
                ?.load(parent!!, content!!)
        }
        val id_toolbar = activity!!.findViewById<View>(R.id.toolbar_id) as Toolbar
        if (id_toolbar != null) {
            id_toolbar.title = ""
            activity.setSupportActionBar(id_toolbar)
            activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
            id_toolbar.setNavigationOnClickListener { onBackListener!!.onBackClick() }

            //标题栏信息

            val index_tv_title =
                activity.findViewById<View>(R.id.toolbar_tv_title) as TextView
            index_tv_title.setTextColor(
                if (titleColor == 0) ContextCompat.getColor(
                    activity,
                    R.color.red
                ) else ContextCompat.getColor(activity, titleColor)
            )
            val relative = activity.findViewById<View>(R.id.relative) as RelativeLayout
            val bgc = activity.findViewById<View>(R.id.bgc) as LinearLayout
            val lp = Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.WRAP_CONTENT
            )
            //标题栏位置
            if (titlePosition == 0) {
                relative.gravity = Gravity.LEFT
                relative.layoutParams = lp
            }
            //标题内容
            index_tv_title.text = title ?: ""
            activity.findViewById<View>(R.id.view_line).visibility =
                if (title == null || title == "") View.GONE else View.VISIBLE
            //标题栏字体类型
            if (fontTypeStr != null) { //字体类型
                val toolbar_tv_title_bttom =
                    activity.findViewById<View>(R.id.toolbar_tv_title_bttom) as TextView
                toolbar_tv_title_bttom.visibility = View.VISIBLE
                val face2 = Typeface.createFromAsset(
                    activity.assets,  /*fontTypeStr*/
                    "fonts/hwcy.ttf"
                )
                toolbar_tv_title_bttom.setTypeface(face2)
                val face = Typeface.createFromAsset(
                    activity.assets,  /*fontTypeStr*/
                    "fonts/hwcy.ttf"
                )
                index_tv_title.setTypeface(face)
            }


            //左边图标
            if (isBack) {
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                id_toolbar.setNavigationIcon(if (iconLeft == 0) R.drawable.btn_back_white else iconLeft)
            } else {
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            }
            //右边图标
            if (iconRight != 0 && rightText == null) {
                val index_iv_right =
                    activity.findViewById<View>(R.id.toolbar_iv_right) as ImageView
                index_iv_right.visibility = View.VISIBLE
                index_iv_right.setBackgroundResource(iconRight)
                index_iv_right.setOnClickListener { onNextListener!!.onNextClick() }
            }
            //右边字体
            if (iconRight == 0 && rightText != null) {
                val toolbar_tv_right_title =
                    activity.findViewById<View>(R.id.toolbar_tv_right_title) as TextView
                toolbar_tv_right_title.text = rightText
                toolbar_tv_right_title.visibility = View.VISIBLE
                toolbar_tv_right_title.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        rightTextColor
                    )
                )
                toolbar_tv_right_title.setOnClickListener { onNextListener!!.onNextClick() }
            }
            //背景色
            if (backgroundColor == 0) {
                bgc.setBackgroundResource(R.drawable.shape_status_color2)
            } else {
                bgc.setBackgroundResource(backgroundColor)
            }
        }
    }


    private fun isView(
        view: View,
        activity: BaseActivityKot,
        onBackListener: OnBackListener?,
        titleColor: Int,
        titlePosition: Int,
        title: String?,
        fontTypeStr: String?,
        isBack: Boolean,
        iconLeft: Int,
        iconRight: Int,
        rightText: String?,
        onNextListener: OnNextListener?,
        rightTextColor: Int,
        backgroundColor: Int
    ) {
        val id_toolbar: Toolbar = view.findViewById(R.id.toolbar_id)
        id_toolbar.title = ""
        activity!!.setSupportActionBar(id_toolbar)
        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
        id_toolbar.setNavigationOnClickListener { onBackListener!!.onBackClick() }

        //标题栏信息
        val index_tv_title = view.findViewById<TextView>(R.id.toolbar_tv_title)
        index_tv_title.setTextColor(
            if (titleColor == 0) ContextCompat.getColor(
                activity,
                R.color.red
            ) else ContextCompat.getColor(activity, titleColor)
        )
        val relative = view.findViewById<RelativeLayout>(R.id.relative)
        val bgc = view.findViewById<View>(R.id.bgc) as LinearLayout
        val lp = Toolbar.LayoutParams(
            Toolbar.LayoutParams.MATCH_PARENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        )
        //标题栏位置
        if (titlePosition == 0) {
            relative.gravity = Gravity.LEFT
            relative.layoutParams = lp
        }
        //标题内容
        index_tv_title.text = title ?: ""
        view.findViewById<View>(R.id.view_line).visibility =
            if (title == null || title == "") View.GONE else View.VISIBLE
        //标题栏字体类型
        if (fontTypeStr != null) { //字体类型
            val toolbar_tv_title_bttom =
                view.findViewById<TextView>(R.id.toolbar_tv_title_bttom)
            toolbar_tv_title_bttom.visibility = View.VISIBLE
            val face2 = Typeface.createFromAsset(
                activity.assets,  /*fontTypeStr*/
                "fonts/hwcy.ttf"
            )
            toolbar_tv_title_bttom.setTypeface(face2)
            val face = Typeface.createFromAsset(
                activity.assets,  /*fontTypeStr*/
                "fonts/hwcy.ttf"
            )
            index_tv_title.setTypeface(face)
        }


        //左边图标
        if (isBack) {
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            id_toolbar.setNavigationIcon(if (iconLeft == 0) R.drawable.btn_back_white else iconLeft)
        } else {
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }

        //右边图标
        if (iconRight != 0 && rightText == null) {
            val index_iv_right = view.findViewById<ImageView>(R.id.toolbar_iv_right)
            index_iv_right.visibility = View.VISIBLE
            index_iv_right.setBackgroundResource(iconRight)
            index_iv_right.setOnClickListener { onNextListener!!.onNextClick() }
        }
        //右边字体
        if (iconRight == 0 && rightText != null) {
            val toolbar_tv_right_title =
                view.findViewById<View>(R.id.toolbar_tv_right_title) as TextView
            toolbar_tv_right_title.text = rightText
            toolbar_tv_right_title.visibility = View.VISIBLE
            toolbar_tv_right_title.setTextColor(
                ContextCompat.getColor(
                    activity,
                    rightTextColor
                )
            )
            toolbar_tv_right_title.setOnClickListener { onNextListener!!.onNextClick() }
        }
        //背景色
        if (backgroundColor == 0) {
            bgc.setBackgroundResource(R.drawable.shape_status_color2)
        } else {
            bgc.setBackgroundResource(backgroundColor)
        }
    }
}

