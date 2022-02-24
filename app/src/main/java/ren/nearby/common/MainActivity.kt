package ren.nearby.common

import ren.nearby.common_module.BaseActivityKot


/**
 * @author:
 * @created on: 2022/2/24 11:28
 * @description:
 */
class MainActivity : BaseActivityKot() {

    override fun initView() {
        super.initView()
        toolBarBuilder2?.let {
            it.setIconLeft(R.drawable.btn_back_white)
                    .setBack(true).setTitle("common")
                    .setBackgroundColor(R.color.colorPrimary)
                    .setTitleColor(R.color.white).build2()

        }
    }

    override fun getLayoutResKot(): Int {
        return R.layout.activity_main
    }
}