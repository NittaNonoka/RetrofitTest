package com.example.retrofittest


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main2.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Main2Activity : AppCompatActivity() {

    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)//一つ前の画面に戻るボタンを表示
        supportActionBar?.title = "RandomUserData"//アクションバーの文字を変更



        // core for controller
        val service = create(ApiService::class.java)

        //非同期にする
        val call: Call<RandomUserDemo> = service.apiDemo()
        call.enqueue(object : Callback<RandomUserDemo> {
            override fun onResponse(call: Call<RandomUserDemo>?, response: Response<RandomUserDemo>) {
                val demo = response.body()
                userText.text ="OK"
                val results =demo?.results
                val userData = results?.get(0)
                var userDataText = ""
                userDataText =
                        userData?.name?.first+"・"+ userData?.name?.last+"\n\n"+
                        userData?.gender+"\n\n"+
                        userData?.dob?.age+"歳"
                if(userData?.location != null){
                    var country=""
                    var state=""
                    var city=""
                    if(userData?.location.country!=null){ country=userData?.location.country }
                    if(userData.location.state!=null){state=userData.location.state}
                    if (userData.location.city!=null){city=userData.location.city}
                    var loc = country+" , "+state+" , "+city
                    locationText.text = loc
                    country=country.replaceAfter(" ","")
                    state=state.replaceAfter(" ","")
                    city=city.replaceAfter(" ","")
                    url.text = "https://www.google.com/maps/search/?api=1&query="+country+","+state+","+city

                    Picasso.get()
                        //画像URL
                        .load(userData.picture.medium)
                        .resize(400, 400) //表示サイズ指定
                        .centerCrop() //resizeで指定した範囲になるよう中央から切り出し
                        .into(image) //imageViewに流し込み

                }else{
                    locationText.text = "未登録"
                }
                userText.text =userDataText
                phoneNumber.text = userData?.cell
                mail.text = userData?.email
            }

            override fun onFailure(call: Call<RandomUserDemo>, t: Throwable) {
                userText.text ="NO"
                Log.d("fetchItems", "response fail")
                Log.d("fetchItems", "throwable :$t")
            }
        })

    }

    //HttpClientを使い、Retrofitフレームワークを経由して APIを定義したinterfaceをインスタンス化します
    fun <S> create(serviceClass: Class<S>): S {
        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        // create retrofit
        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://randomuser.me") // ベースURL
            .client(httpBuilder.build())
            .build()

        return retrofit.create(serviceClass)
    }

    //<-ボタンを押した時の処理
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    //HttpCliantを作成する
    val httpBuilder: OkHttpClient.Builder get() {
        // create http client
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()

                //header
                val request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()

                return@Interceptor chain.proceed(request)
            })
            .readTimeout(30, TimeUnit.SECONDS)

        // log interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)
        return httpClient

    }

}
