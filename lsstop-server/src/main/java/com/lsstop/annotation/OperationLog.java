package com.lsstop.annotation;

import com.lsstop.enums.OperationModuleEnum;
import com.lsstop.enums.OperationTypeEnum;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author lishusheng
 * @date 2026/03/29
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 操作模块
     */
    OperationModuleEnum module();

    /**
     * 操作类型
     */
    OperationTypeEnum type();

    /**
     * 操作描述
     */
    String description() default "";

}
