package mkgeeklab.study.weatherapp

import java.util.Date

enum class Temperature2m(val label: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
}

data class WeatherCity(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val temperature2m: Temperature2m = Temperature2m.CELSIUS,
    val lastUpdate: Date,
)

data class WeatherInfo(
    val city: WeatherCity,
    val time: Date,
    val weatherCode: Int,
    val temperature: Double,
    val temperature2m: Temperature2m,
    val lastUpdate: Date,
)
