package com.berry.traveldiary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityDiaryEntryBinding
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


class DiaryEntryActivity : AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityDiaryEntryBinding
    private lateinit var myDatabase: MyDatabase
    lateinit var locationManager: LocationManager
    private val LOCATION_REQUEST_CODE = 1001
    private var SelectedImageUri: String = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val MIN_TIME_BETWEEN_UPDATES = 1000L // 1 second
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f // 1 meter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryEntryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@DiaryEntryActivity)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        checkLocationSettings()


        // Check for location permissions and request if not granted
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            startLocationUpdates()
        }

        binding.edtDate.setOnClickListener {
            CommonUtils.openDatePicker(binding.edtDate, supportFragmentManager)
        }

        binding.accountImage.setOnClickListener {
            ImagePicker.with(this@DiaryEntryActivity)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080, 1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(102)
        }

        //saving the diary entry
        binding.btnSave.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }

            //to dismiss keyboard
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            saveDiaryEntry(it)
        }

    }

    private fun checkLocationSettings() {
        val locationRequest = create().apply {
            priority = PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                // All location settings are satisfied. You can request location updates here.
                // Start your location updates function or any other location-related functionality.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        // Location settings are not satisfied, but this can be fixed by showing
                        // the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            val resolvable: ResolvableApiException =
                                exception as ResolvableApiException
                            resolvable.startResolutionForResult(
                                this@DiaryEntryActivity, LOCATION_REQUEST_CODE
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. However, we have no way to fix
                        // the settings, so we won't show the dialog.
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        binding.progressBar.visibility = View.VISIBLE
        try {
            // Request location updates
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                this
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun saveDiaryEntry(view: View) {
        if (isValid(view)) {
            insertTOdb(view)
        }

    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current location address", strReturnedAddress.toString())
            } else {
                Log.w("My Current location address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current location address", "Cannot get Address!")
        }
        return strAdd
    }

    private fun insertTOdb(view: View) {
        val title = binding.edtTitle.text.toString()
        val date = binding.edtDate.text.toString()
        val location = binding.edtLocation.text.toString()
        val desc = binding.edtDesc.text.toString()
        val img = SelectedImageUri


        val loginData = CommonUtils.getStringPref(CommonUtils.PREF_LOGIN, this)
        Log.d("TAG", "loginData>>$loginData")
        if (!TextUtils.isEmpty(loginData)) {
            val user: User = Gson().fromJson(loginData, User::class.java)


            val diaryEntries = DiaryEntries(0, title, date, location, desc, user.id, img)


            CoroutineScope(Dispatchers.IO).launch {
                myDatabase.diaryEntryDao().addDiaryEntry(diaryEntries)
            }.invokeOnCompletion {
                Snackbar.make(view, "New Diary Entry Added", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                val returnIntent = Intent()
                returnIntent.putExtra("result", 122)
                setResult(RESULT_OK, returnIntent)
                finish()
            }
        }

    }

    private fun isValid(view: View): Boolean {
        val title = binding.edtTitle.text.toString()
        val date = binding.edtDate.text.toString()
        val location = binding.edtLocation.text.toString()
        val desc = binding.edtDesc.text.toString()
        return if (TextUtils.isEmpty(title)) {
            Snackbar.make(view, "Please enter title", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else if (TextUtils.isEmpty(date)) {
            Snackbar.make(view, "Please enter date", Snackbar.LENGTH_LONG).setAction("Action", null)
                .show()
            false
        } else if (TextUtils.isEmpty(location)) {
            Snackbar.make(view, "Please enter location", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else if (TextUtils.isEmpty(desc)) {
            Snackbar.make(view, "Please enter description", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            false
        } else {
            true
        }
    }


    override fun onLocationChanged(location: Location) {
        binding.progressBar.visibility = View.GONE
        val latitude = location.latitude
        val longitude = location.longitude
        val stringLocation = getCompleteAddressString(latitude, longitude)
        binding.edtLocation.setText(stringLocation)

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationUpdates()

                // The user has enabled the location settings.
                // You can now start your location updates function or any other location-related functionality.
            } else {
                binding.progressBar.visibility = View.GONE
                // The user has not enabled the location settings.
                // You can handle this situation as per your app's requirement.
            }
        }
        if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri = data?.data!!
                SelectedImageUri = uri.toString()
                binding.accountImage.setImageURI(uri)
            }
        }
    }
}