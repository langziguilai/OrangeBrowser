package com.dev.orangebrowser.data.remote

import com.dev.orangebrowser.data.model.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface NewsApi {
    @GET("news/{categoryId}")
    fun list(@Path("categoryId") categoryId: String,@Query("id") id:String): Call<List<News>>
}

class MockNewsApi:NewsApi{
    override fun list(categoryId: String, id: String): Call<List<News>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}