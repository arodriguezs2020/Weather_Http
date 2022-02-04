package data

import android.app.Activity
import android.content.SharedPreferences

class PreferenceCiudad(activity: Activity) {

    var prefs : SharedPreferences

    init {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE)
    }

    var ciudad : String?
    get() = prefs.getString("ciudad", "Merida,MX")
    set(ciudad) = prefs.edit().putString("ciudad", ciudad).apply()
}
