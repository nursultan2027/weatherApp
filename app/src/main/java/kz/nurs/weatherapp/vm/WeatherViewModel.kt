package kz.nurs.weatherapp.vm


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import kz.nurs.weatherapp.model.ApiResponse
import kz.nurs.weatherapp.network.Repository

class WeatherViewModel(): ViewModel(){
    private val disposable = CompositeDisposable()
    private val response = MutableLiveData<ApiResponse>()

    fun weatherResponse(): MutableLiveData<ApiResponse> {
        return response
    }

    fun getWeather(lon: Double, lat: Double, id: String){
        disposable.add(
            Repository.newInstance().getWeatherByLocation(lon, lat, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { response.value= ApiResponse.loading()}
            .subscribe(
                { result ->
                    if(result.code()==200){
                        response.value= ApiResponse.success(result)
                    } else {
                        response.value= ApiResponse.error(result)
                    }
                },
                { throwable ->  response.value= ApiResponse.throwable(throwable)}
            ))
    }
}
