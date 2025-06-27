package mkgeeklab.study.weatherapp

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun WeatherMapView(
    modifier: Modifier = Modifier,
    weatherList: List<WeatherInfo> = emptyList<WeatherInfo>(),
) {

    val tokyo = LatLng(35.68, 139.76)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tokyo, 5f)
    }
    var selectedInfo by remember { mutableStateOf<WeatherInfo?>(null) }
    val onMarkerClick:  (Marker) -> Boolean = { marker ->
        (marker.tag as? WeatherInfo).let { tag -> selectedInfo = tag }
        true
    }
    val onMapClick: (LatLng) -> Unit = {
        selectedInfo = null
    }


    Box(
        modifier = modifier,
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            onMapClick = onMapClick,
        ) {
            weatherList.forEach { info ->
                val state = MarkerState().also {
                    it.position = LatLng(info.city.latitude, info.city.longitude)
                }
                val iconResId = getWeatherIconRes(info.weatherCode)

                Marker(
                    state = state,
                    tag = info,
                    icon = BitmapDescriptorFactory.fromResource(iconResId),
                    onClick = onMarkerClick
                )
            }
        }

        selectedInfo?.let { info ->
            WeatherRow(
                weather = info
            )
        }
    }
}
