package ren.nearby.common_module.scope

import dagger.Subcomponent
import dagger.android.AndroidInjector
import ren.nearby.common_module.BaseActivityKot

/**
 * @author:
 * @created on: 2022/2/24 13:42
 * @description:
 */
@Subcomponent(modules = [AndroidInjector::class])
interface BaseActivityComponentKt : AndroidInjector<BaseActivityKot> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BaseActivityKot>()
}