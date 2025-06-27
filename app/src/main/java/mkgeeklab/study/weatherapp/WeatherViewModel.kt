package mkgeeklab.study.weatherapp

import android.app.Application
import android.icu.util.Calendar
import android.location.Geocoder
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mkgeeklab.study.weatherapp.db.WeatherDatabase
import mkgeeklab.study.weatherapp.db.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import java.util.Locale

interface WeatherViewModel {
    val weatherList: StateFlow<List<WeatherInfo>>
    val cities: StateFlow<List<WeatherCity>>
    fun loadWeather()
}

// --- ViewModel ---
class WeatherViewModelImpl(application: Application) : AndroidViewModel(application), WeatherViewModel {
    private val _weatherList = MutableStateFlow<List<WeatherInfo>>(emptyList())
    override val weatherList: StateFlow<List<WeatherInfo>> = _weatherList
    private lateinit var apiService: WeatherApiService

    private val _cities = MutableStateFlow<List<WeatherCity>>(emptyList())
    override val cities: StateFlow<List<WeatherCity>> = _cities

    private val geocoder = Geocoder(application, Locale.getDefault())

    private val db = Room.databaseBuilder(
            application,
            WeatherDatabase::class.java,
            "weather_database.db",
        )
        .addMigrations(WeatherRepository.Migration1_2)
        .build()

    private val repository = WeatherRepository(
        cityDao = db.cityDao(),
        forecastDao = db.weatherForecastDao(),
    )

    init {
        viewModelScope.launch {
            val hasAnyCity = repository.existAnyCity()
            if (!hasAnyCity) {
                saveInitCities()
            }

            _cities.value = repository.loadWeatherCities()
            loadWeather()
        }
    }

    suspend fun saveInitCities() {
        val now = Date()
        val cities = listOf(
            WeatherCity(1, "Tokyo", 35.68, 139.76, Temperature2m.CELSIUS, now),
            WeatherCity(2, "Osaka", 34.69, 135.50, Temperature2m.CELSIUS, now),
            WeatherCity(3, "Sapporo", 43.06, 141.34, Temperature2m.CELSIUS, now),
            WeatherCity(4, "Fukuoka", 33.59, 130.40, Temperature2m.CELSIUS, now),
        )

        repository.saveWeatherCities(cities)
    }

    override fun loadWeather() {
        val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/") // APIの共通URLは必ず最後を/にしなければならない
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        apiService = retrofit.create(WeatherApiService::class.java)

        viewModelScope.launch {
            try {
                val forecasts = getDummyWeather()
                _weatherList.value = forecasts
                repository.saveWeatherInfo(forecasts)
            } catch (e: Exception) {
                e.printStackTrace()

                // ネット失敗時はDBから読み込み
                val cached = repository.getWeatherInfo(_cities.value)

                if (cached.isNotEmpty()) {
                    _weatherList.value = cached
                } else {
                    // DBにも何もない場合は空のまま
                    Toast.makeText(application, "データ取得失敗", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getDummyWeather(): List<WeatherInfo> {
        val currentHourIdx = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return _cities.value.map { city ->
            apiService
                .getForecast(
                    latitude = city.latitude,
                    longitude = city.longitude,
                )
                .toWeatherInfoList(city)
                .get(currentHourIdx)
        }
    }
}