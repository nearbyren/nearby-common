package ren.nearby.common_module.scope;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * In Dagger, an unscoped component cannot depend on LocationService scoped component. As
 * {@link   } is LocationService scoped component ({@code @Singleton}, we create LocationService custom
 * scope to be used by finances_all fragment components. Additionally, LocationService component with LocationService specific scope
 * cannot have LocationService sub component with the same scope.
 * 1、RetentionPolicy.SOURCE：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；
 * 2、RetentionPolicy.CLASS：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期；
 * 3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在；
 **/

@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ContractScoped {
}
