package com.dev.base.exception

import com.dev.util.Keep
import com.dev.util.KeepMemberIfNecessary

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
@Keep
sealed class Failure(var error:String) {
    @KeepMemberIfNecessary
    object NetworkConnection : Failure("网络发生错误")
    @KeepMemberIfNecessary
    object IOError : Failure("文件发生错误")
    @KeepMemberIfNecessary
    object ServerError : Failure("服务器端错误")  //5xx
    @KeepMemberIfNecessary
    object AuthError:Failure("无此访问权限")   //4xx
    @KeepMemberIfNecessary
    object NotFoundError:Failure("未找到相应内容") //404
    @KeepMemberIfNecessary
    object OtherRequestError:Failure("其他请求错误") //404
    /** * Extend this class for feature specific failures.*/
    @Keep
    abstract class FeatureFailure: Failure("")
}
