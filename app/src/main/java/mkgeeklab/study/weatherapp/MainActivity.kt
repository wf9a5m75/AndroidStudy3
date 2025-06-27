package mkgeeklab.study.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mkgeeklab.study.weatherapp.GlobalViewModelStore
import mkgeeklab.study.weatherapp.ui.theme.WeatherAppTheme
import java.util.Date
import kotlin.getValue
import kotlin.math.abs

// --- Icon Mapping Table ---
val weatherIconMap = mapOf(
    0 to R.drawable.wmo_00_clear,
    1 to R.drawable.wmo_01_mostly_clear,
    2 to R.drawable.wmo_02_partly_cloudy,
    3 to R.drawable.wmo_03_overcast,
    17 to R.drawable.wmo_17_snow_showers,
    45 to R.drawable.wmo_45_fog,
    48 to R.drawable.wmo_48_fog,
    51 to R.drawable.wmo_51_light_drizzle,
    53 to R.drawable.wmo_53_drizzle,
    55 to R.drawable.wmo_55_heavy_drizzle,
    56 to R.drawable.wmo_56_light_icy_drizzle,
    57 to R.drawable.wmo_57_icy_drizzle,
    61 to R.drawable.wmo_61_light_rain,
    63 to R.drawable.wmo_63_rain,
    65 to R.drawable.wmo_65_heavy_rain,
    66 to R.drawable.wmo_66_light_icy_rain,
    67 to R.drawable.wmo_67_icy_rain,
    71 to R.drawable.wmo_71_ligth_snow,
    73 to R.drawable.wmo_73_snow,
    75 to R.drawable.wmo_75_heavy_snow,
    80 to R.drawable.wmo_80_light_showers,
    81 to R.drawable.wmo_81_showers,
    82 to R.drawable.wmo_82_heavy_showers,
    85 to R.drawable.wmo_85_light_snow_showers,
    86 to R.drawable.wmo_86_snow_showers,
    95 to R.drawable.wmo_95_thunder_storm,
    96 to R.drawable.wmo_96_thunder_storm_with_light_hail,
    99 to R.drawable.wmo_99_thunder_storm_with_hail,
)

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: WeatherViewModelImpl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            GlobalViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(WeatherViewModelImpl::class.java)

        setContent {
            WeatherAppTheme {
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: WeatherViewModel) {
    val weatherList by viewModel.weatherList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather App") }
            )
        }
    ) { paddingValues ->
        WeatherTabScreen(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            weatherList = weatherList,
        )
    }
}

@Composable
fun WeatherTabScreen(
    weatherList: List<WeatherInfo>,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf( "リスト", "地図")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (selectedTabIndex) {
            0 -> {
                WeatherList(
                    weatherList = weatherList,
                    modifier = Modifier.fillMaxSize()
                )
            }
            1 -> {
                WeatherMapView(
                    modifier = Modifier.fillMaxSize(),
                    weatherList = weatherList
                )
            }
        }
    }
}

@Composable
fun WeatherList(
    weatherList: List<WeatherInfo> = emptyList<WeatherInfo>(),
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weatherList.size) { index ->
            val weather = weatherList[index]
            WeatherRow(weather)
        }
    }
}

@Composable
fun WeatherRow(weather: WeatherInfo = WeatherInfo(
    city = WeatherCity(
        id = 1,
        name = "Preview",
        latitude = 0.0,
        longitude = 0.0,
        lastUpdate = Date(),
    ),
    time = Date(),
    weatherCode = 0,
    temperature = 22.0,
    temperature2m = Temperature2m.CELSIUS,
    lastUpdate = Date(),
)) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weather.city.name,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val iconRes = getWeatherIconRes(weather.weatherCode)
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${weather.temperature}°",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
fun getWeatherIconRes(weatherCode: Int): Int {
    return weatherIconMap[weatherCode]
        ?: weatherIconMap.entries.minByOrNull { abs(it.key - weatherCode) }?.value
        ?: R.drawable.wmo_00_clear
}

@Preview(
    device = Devices.DEFAULT,
    heightDp = 600,
)
@Composable
fun PreviewMainScreen() {
    MainScreen(object : WeatherViewModel {
        override val weatherList: StateFlow<List<WeatherInfo>>
            get() = MutableStateFlow(emptyList())
        override val cities: StateFlow<List<WeatherCity>>
            get() = TODO("Not yet implemented")

        override fun loadWeather() {
            TODO("Not yet implemented")
        }

    })
}