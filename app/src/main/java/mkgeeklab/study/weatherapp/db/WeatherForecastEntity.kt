package mkgeeklab.study.weatherapp.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "weather_forecast",
    foreignKeys = [
        ForeignKey(
            entity = CityEntity::class,
            parentColumns = ["city_id"],  // CityEntityクラスのcity_id
            childColumns = ["city_id"],   // WeatherForecastEntityクラスのcity_id
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class WeatherForecastEntity(
    @PrimaryKey(autoGenerate = true) val forecast_id: Long = 0L,
    val city_id: Long,        // 都市ID
    val time: Long,           // 予報日時 Unix time
    val weatherCode: Int,     // 天気コード
    val temperature: Double,  // 気温
    val lastUpdate: Long      // 最終更新日時（キャッシュの新しさを判定）
)