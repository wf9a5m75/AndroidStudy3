package mkgeeklab.study.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("timezone") timezone: String = "Asia/Tokyo"
    ): WeatherApiResponse
}