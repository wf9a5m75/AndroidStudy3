package mkgeeklab.study.weatherapp

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WeatherApiResponse(
    val latitude: Double,
    val longitude: Double,
    val utc_offset_seconds: Int,
    val hourly_units: HourlyUnits,
    val hourly: HourlyWeather,
    val last_update: Date = Date(),
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val weathercode: String
)

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weathercode: List<Int>
)

fun WeatherApiResponse.toWeatherInfoList(city: WeatherCity): List<WeatherInfo> {
    // 単位をenumに変換
    val tempUnit = when (hourly_units.temperature_2m) {
        "°C" -> Temperature2m.CELSIUS
        "°F" -> Temperature2m.FAHRENHEIT
        else -> Temperature2m.CELSIUS // デフォルト
    }

    // hourlyデータをWeatherInfoリストに変換
    return hourly.time.indices.map { i ->
        WeatherInfo(
            city = city,
            time = parseIsoStringToDate(hourly.time[i], utc_offset_seconds),
            weatherCode = hourly.weathercode[i],
            temperature = hourly.temperature_2m[i],
            temperature2m = tempUnit,
            lastUpdate = last_update,
        )
    }
}

fun parseIsoStringToDate(dateString: String, offsetSeconds: Int): Date {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // AP 26以上 (安全：高い、パフォーマンス：良い）
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val localDateTime = LocalDateTime.parse(dateString, formatter)
        val offset = ZoneOffset.ofTotalSeconds(offsetSeconds)
        Date.from(localDateTime.toInstant(offset))
    } else {
        // API 25以下
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val parsedDate = formatter.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
        Date(parsedDate.time - offsetSeconds * 1000L)
    }
}

