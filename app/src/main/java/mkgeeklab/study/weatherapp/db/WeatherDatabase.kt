package mkgeeklab.study.weatherapp.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CityEntity::class,
        WeatherForecastEntity::class
    ],
    version = 2,  // バージョンアップが必須！
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun weatherForecastDao(): WeatherForecastDao
}