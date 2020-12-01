package kz.nurs.weatherapp.network

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import kz.nurs.weatherapp.model.WeatherResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Repository {
    private var mainService: Service? = null

    private constructor (){
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .method(original.method(), original.body())
            chain.proceed(builder.build())
        }.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()
        val rxAdapter = RxJava2CallAdapterFactory.create()
        mainService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(clientBuilder.build())
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create<Service>(Service::class.java)
    }

    companion object {
        @JvmStatic
        fun newInstance(): Repository{
            return Repository()
        }
    }

    fun getWeatherByLocation(lon: Double, lat: Double, id: String): Observable<Response<WeatherResponse>> {
        return mainService!!.getWeatherByLocation(lon, lat, id)
    }
}