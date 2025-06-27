package mkgeeklab.study.weatherapp.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import mkgeeklab.study.weatherapp.Temperature2m
import mkgeeklab.study.weatherapp.WeatherCity
import mkgeeklab.study.weatherapp.WeatherInfo
import java.util.Date

class WeatherRepository(
    private val cityDao: CityDao,
    private val forecastDao: WeatherForecastDao,
) {
    suspend fun saveWeatherInfo(infoList: List<WeatherInfo>) {
        val entityList = infoList.map { info ->
            WeatherForecastEntity(
                city_id = info.city.id,
                time = info.time.time,
                weatherCode = info.weatherCode,
                temperature = info.temperature,
                lastUpdate = info.lastUpdate.time
            )
        }
        forecastDao.insertForecasts(entityList)
    }

    suspend fun getWeatherInfo(cities: List<WeatherCity>): List<WeatherInfo> {
        var forecasts = mutableListOf<WeatherInfo>()
        cities.forEach { city ->
            val entities = forecastDao.getForecasts(city.id)
            entities.forEach {
                val info = WeatherInfo(
                    city = city,
                    time = Date(it.time),
                    weatherCode = it.weatherCode,
                    temperature = it.temperature,
                    temperature2m = city.temperature2m,
                    lastUpdate = Date(it.lastUpdate)
                )
                forecasts.add(info)
            }
        }
        return forecasts
    }

    suspend fun existAnyCity(): Boolean = cityDao.countCity() > 0

    suspend fun saveWeatherCities(cityList: List<WeatherCity>) {
        val entityList = cityList.map { city ->
            CityEntity(
                name = city.name,
                latitude = city.latitude,
                longitude = city.longitude,
                use_celsus = when (city.temperature2m) {
                    Temperature2m.CELSIUS -> 1
                    Temperature2m.FAHRENHEIT -> 0
                },
                last_update = city.lastUpdate.time // Long型（ミリ秒）
            )
        }
        cityDao.insertCities(entityList)
    }

    suspend fun loadWeatherCities(): List<WeatherCity> {
        return cityDao.getAllCities().map { entity ->
            WeatherCity(
                id = entity.city_id,
                name = entity.name,
                latitude = entity.latitude,
                longitude = entity.longitude,
                temperature2m = if (entity.use_celsus == 1)
                    Temperature2m.CELSIUS else Temperature2m.FAHRENHEIT,
                lastUpdate = Date(entity.last_update)
            )
        }
    }

    companion object {
        val Migration1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS weather_forecast (
                        forecast_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        city_id INTEGER NOT NULL,
                        time INTEGER NOT NULL,
                        weatherCode INTEGER NOT NULL,
                        temperature REAL NOT NULL,
                        lastUpdate INTEGER NOT NULL,
                        FOREIGN KEY (city_id) REFERENCES city(city_id) ON DELETE CASCADE
                    );
                """.trimIndent())
            }
        }
    }
}
