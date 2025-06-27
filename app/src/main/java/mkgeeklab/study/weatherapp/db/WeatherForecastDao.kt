package mkgeeklab.study.weatherapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<WeatherForecastEntity>)

    @Query("SELECT * FROM weather_forecast WHERE city_id = :city_id ORDER BY time ASC")
    suspend fun getForecasts(city_id: Long): List<WeatherForecastEntity>

}