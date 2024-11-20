package com.berry.traveldiary.uitility

import android.R
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object CommonUtils {


    const val PREFERENCES: String = "MySharedPref"
    const val NO_IMAGE: String = "no_image"
    const val PREF_LOGIN: String = "login_info"
    const val PREF_ENTRY_ID: String = "entry_id"
    const val PREF_USER_IMG: String = "img_user"

    private lateinit var snackbar: Snackbar

    fun setStringPref(key: String, value: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val myEdit = sharedPreferences.edit()
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(key, value)
        myEdit.apply()
    }

    fun getStringPref(key: String, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            PREFERENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        return sharedPreferences.getString(key, "")

    }

    fun showCustomSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    fun showSnackbar(view: View, message: String, isForSuccess: Boolean) {
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        // Customize the Snackbar's appearance here
        val snackbarView = snackbar.view
        val red = view.resources.getColor(R.color.holo_red_light)
        val green = view.resources.getColor(R.color.holo_green_light)

        if (isForSuccess) {
            snackbarView.setBackgroundColor(green)
        } else {
            snackbarView.setBackgroundColor(red)
        }


        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)

        snackbar.show()
    }


    fun openDatePicker(textView: TextView, supportFragmentManager: FragmentManager) {
        // Create a MaterialDatePicker instance
        val builder = MaterialDatePicker.Builder.datePicker()
        val datePicker = builder.build()

        // Set a listener to handle the selected date
        datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateInMillis
            // Do something with the selected date
            // For example, display the selected date in a TextView
            textView.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        }

        // Show the date picker dialog
        datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(email)
    }

    suspend fun delayedFunction() {
        delay(500)
    }

}