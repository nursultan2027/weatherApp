package kz.nurs.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kz.nurs.weatherapp.CustomObjectAnimator
import kz.nurs.weatherapp.R
import kz.nurs.weatherapp.model.ApiResponse
import kz.nurs.weatherapp.model.WeatherResponse
import kz.nurs.weatherapp.vm.WeatherViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this)
        setContentView(R.layout.activity_main)
        viewModel = WeatherViewModel()
        viewModel.weatherResponse().observe(this, Observer { consumeResponse(it) })
        showInfoFromSaved()
        if (!checkLocationPermission()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
        animateIcons()
    }

    private fun animateIcons() {
        CustomObjectAnimator.animateTab(iv_pressureIcon)
        CustomObjectAnimator.animateTab(iv_tempIcon)
        CustomObjectAnimator.animateTab(iv_windIcon)
    }

    private fun consumeResponse(response: ApiResponse) {
        when (response.status){
            ApiResponse.Status.SUCCESS->{
                response.data?.let {
                    showInfo(it.body() as WeatherResponse)
                    saveToLocal(it.body() as WeatherResponse)
                }
                progress_layout.visibility= View.GONE
            }
            ApiResponse.Status.ERROR->{
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
                progress_layout.visibility= View.GONE
            }
            ApiResponse.Status.LOADING->{
                progress_layout.visibility= View.VISIBLE
            }
        }
    }

    private fun saveToLocal(weatherResponse: WeatherResponse) {
        Paper.book().write("savedWeather", weatherResponse)
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 203)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            203->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation()
                } else {
                    requestLocationPermissions()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location= lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val longitude: Double = location.longitude
        val latitude: Double = location.latitude

        Runnable {
            viewModel.getWeather(longitude, latitude, "a2ab2deff73dcef19735a47401e24278")
        }.let {
            Handler().postDelayed(it, 2000)
        }
    }

    private fun showInfoFromSaved() {
        val savedWeather = Paper.book().read<WeatherResponse>("savedWeather", null)
        savedWeather?.let {
            showInfo(savedWeather)
        }
    }

    private fun showInfo(weatherResponse: WeatherResponse){
        tv_location.text = weatherResponse.name
        tv_temp.text = "Температура: ${weatherResponse.main?.temp.toString()}"
        tv_tempFeels.text = "Ощущается: ${weatherResponse.main?.feelsLike.toString()}"
        tv_tempMin.text = "Максимальная: ${weatherResponse.main?.tempMin.toString()}"
        tv_tempMax.text = "Минимальная: ${weatherResponse.main?.tempMax.toString()}"
        tv_pressure.text = "Давление: ${weatherResponse.main?.pressure.toString()}"
        tv_windSpeed.text ="Скорость ветра: ${weatherResponse.wind?.speed.toString()}"
    }

}