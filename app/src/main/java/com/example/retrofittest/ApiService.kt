package com.example.retrofittest

import retrofit2.Call
import retrofit2.http.GET

//APIのインターフェースを定義
interface ApiService {
    @GET("api")//ここで定義するのは規定URL https://randomuser.me/ 以降の部分
    fun apiDemo(): Call<RandomUserDemo>//CallはRetrofitに用意されているレシーバクラス
}