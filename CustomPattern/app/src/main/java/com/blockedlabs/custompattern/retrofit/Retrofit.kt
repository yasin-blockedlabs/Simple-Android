package com.blockedlabs.custompattern.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {

    private val BASE_URL = "http://"

    lateinit var retrofit: retrofit2.Retrofit

    private lateinit var apiService: ApiService

    init {
        loadRetrofit()
    }

    private fun loadRetrofit(){
        retrofit = createRetrofit()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun createRetrofit(): retrofit2.Retrofit{
        val httpClientBuilder = OkHttpClient.Builder()
        val interceptor = object: Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + getToken())
                    .build()
                return chain.proceed(request)
            }
        }
        val logging = HttpLoggingInterceptor(object: HttpLoggingInterceptor.Logger{
            override fun log(message: String) {
                Log.i("okhttp", message)
            }
        })
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = httpClientBuilder.addInterceptor(interceptor).addInterceptor(logging).build()
        return retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

    fun getService(): ApiService = apiService

    fun getToken(): String? = ""

    companion object{

        @Volatile private var instance: Retrofit? = null

        fun getInstance(): Retrofit {
            val checkInstance = instance
            if(checkInstance != null){
                return checkInstance
            }

            return synchronized(this){
                val checkInstanceAgain = instance
                if(checkInstanceAgain != null){
                    checkInstanceAgain
                }else{
                    val created = Retrofit()
                    instance = created
                    created
                }
            }
        }
    }

}