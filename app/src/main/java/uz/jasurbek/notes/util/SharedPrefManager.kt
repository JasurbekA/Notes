package uz.jasurbek.notes.util


import android.content.Context
import android.content.SharedPreferences
import uz.jasurbek.notes.data.Constants
import javax.inject.Inject

class SharedPrefManager @Inject constructor(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun saveString(key: String, text: String) = with(sharedPref.edit()) {
        putString(key, text)
        apply()
    }

    fun getValueString(key: String, defValue: String = ""): String? =
        sharedPref.getString(key, defValue)

}