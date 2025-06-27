package mkgeeklab.study.weatherapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CityDao {
    @Query("SELECT COUNT(*) as CNT FROM city")
    suspend fun countCity(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityEntity>)

    @Query("SELECT * FROM city")
    suspend fun getAllCities(): List<CityEntity>

    @Query("DELETE FROM city where city_id = :city_id")
    suspend fun deleteCityBy(city_id: Long)
}

