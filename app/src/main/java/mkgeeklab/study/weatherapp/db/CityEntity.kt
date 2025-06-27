package mkgeeklab.study.weatherapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val city_id: Long = 0L,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val use_celsus: Int,
    val last_update: Long,
)
