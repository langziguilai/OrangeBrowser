package com.dev.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @doc 如果有必要(当类被保留时)，则保留Class的Member的名称不被混淆,(无用的方法，变量还是会被删除)
 * */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
public @interface KeepMemberIfNecessary { }
