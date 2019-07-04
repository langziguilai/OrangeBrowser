package com.dev.base.extension

import com.dev.base.exception.Failure
import com.dev.util.Keep
import retrofit2.Call
@Keep
fun <T> Call<T>.onResult(onSuccess:(T?)->Unit, onFail:(Failure)->Unit){
    val response=execute()
    if (response.isSuccessful){
        onSuccess(response.body())
    }else{
        if (response.code()>=500){
            onFail(Failure.ServerError)
        }else if (response.code()==401 || response.code()==403 || response.code()==405){
            onFail(Failure.AuthError)
        }else if (response.code()==404){
            onFail(Failure.NotFoundError)
        }else{
            onFail(Failure.OtherRequestError)
        }
    }
}