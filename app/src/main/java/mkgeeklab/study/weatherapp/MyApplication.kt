package mkgeeklab.study.weatherapp

import android.app.Application

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // この行を呼び出さなければダイナミックカラーは適用されません
        // DynamicColors.applyToActivitiesIfAvailable(this)
    }
}