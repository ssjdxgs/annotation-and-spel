package com.ssjdxgs.checkparam.annotation;

import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * @author ssjdxgs
 */
@Target({ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CheckParams.class)
public @interface CheckParam {

    /**
     * CheckParamRoot类中的方法
     */
    @Language(value = "SpEL",prefix = "T(com.ssjdxgs.checkparam.check.CheckParamRoot).")
    String value() default "";

    /**
     * 自定义bean中的方法
     */
    @Language(value = "SpEL")
    String beanMethod() default "";

    String message() default "参数不合法!";
}

