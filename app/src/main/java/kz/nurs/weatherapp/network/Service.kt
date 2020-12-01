package kz.nurs.weatherapp.network

import io.reactivex.Observable
import kz.nurs.weatherapp.model.WeatherResponse
import retrofit2.http.*

interface Service {
    @POST("data/2.5/weather")
    fun getWeatherByLocation(@Query("lon") lon: Double, @Query("lat") lat: Double, @Query("appid") id: String): Observable<retrofit2.Response<WeatherResponse>>
}

