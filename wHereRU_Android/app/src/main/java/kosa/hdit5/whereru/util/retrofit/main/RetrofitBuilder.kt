package kosa.hdit5.whereru.util.retrofit.main

import com.google.gson.GsonBuilder
import kosa.hdit5.whereru.MyCookieJar
import kosa.hdit5.whereru.util.retrofit.main.`interface`.WhereRUAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    //baseUrl은 오픈 api의 서버 url을 넣는다.
    var baseUrl: String = "http://10.0.2.2:8080/"
    var api: WhereRUAPI
    var gson = GsonBuilder().setLenient().create()
    val cookieJar = MyCookieJar()
    val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            //json을 gson으로 파싱할거니까 GsonConverterFactory.create()로 gsonconverter를 가져온다.
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build() //Retrofit 객체 생성

        api = retrofit.create(WhereRUAPI::class.java)
    }

}