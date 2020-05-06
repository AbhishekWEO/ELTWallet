package com.crypto.eltwallet.activities

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.fingerprint.FingerprintManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.crypto.eltwallet.R
import com.crypto.eltwallet.fingerPrintsInterface.FingerPrintAuthCallback
import com.crypto.eltwallet.interfaces.LanguageChange
import com.crypto.eltwallet.utilities.*
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*

class SplashActivity : AppCompatActivity(), FingerPrintAuthCallback, LanguageChange {

    var rl_main : RelativeLayout?=null
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    var location: Location? = null
    var addresses: List<Address>? = null
    lateinit var address: Address
    var geocoder: Geocoder? = null
    var countryCode: String=""
    private var isLocationEnabled: Boolean = false
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mLocationManager: LocationManager

    private var mFingerPrintAuthHelper: FingerPrintAuthHelper? = null
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        rl_main = findViewById(R.id.FrameLayout01)
        sharedPreferenceUtil = SharedPreferenceUtil(this)



        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun locdata()
    {
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "")?.length !== 0)
        {
            Handler().postDelayed({
                if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                {
                    startActivity(Intent(this, SecurePinLoginActivity::class.java))
                    finish()
                }
                else
                {
                    startActivity(Intent(this, BaseDashboardActivity::class.java))
                    finish()
                }


            }, 3000)

        }
        else
        {

            val strArr = arrayOf<String>(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            if (!hasPermissions(this, *strArr))
            {
                Handler().postDelayed({
                    ActivityCompat.requestPermissions(this, strArr, 1)
                }, 3000)

            }
            else
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("request14", "permissions not granted")
                }
                else
                {
                    Log.e("request15", "permissions granted")

                    try
                    {

                        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                        locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        geocoder = Geocoder(this)

                        if (location==null)
                        {

                            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            isLocationEnabled=false
                            checkLocation()
                        }
                        else
                        {
                            isLocationEnabled=true
                            addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
                            var address : Address = addresses!!.get(0)
                            countryCode =  address.getCountryCode()

                            val intent = Intent(this, AuthActivity::class.java)
                            intent.putExtra("countryCode", countryCode)
                            startActivity(intent)
                            finish()
                        }

                    } catch (e : IOException) {
                        e.printStackTrace();
                    }

                }
            }

//            Handler().postDelayed({
//                val intent = Intent(this, AuthActivity::class.java)
//                intent.putExtra("countryCode", "IN")
//                startActivity(intent)
//                finish()
//
//            }, 3000)

        }

    }

    private fun hasPermissions(context2: Context?, vararg strArr: String): Boolean {
        if (!(Build.VERSION.SDK_INT < 23 || context2 == null || strArr == null)) {
            for (checkSelfPermission in strArr) {
                if (ActivityCompat.checkSelfPermission(context2, checkSelfPermission) !== 0) {
                    return false
                }
            }
        }
        return true
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1)
        {
            Log.e("request1", "request1")

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.e("request12", "permissions not granted")

            }
            else
            {
                Log.e("request13", "permissions granted")

                try
                {
                    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location==null)
                    {
                        //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        isLocationEnabled=false
                        checkLocation()
                    }
                    else
                    {
                        isLocationEnabled=true

                        geocoder = Geocoder(this)
                        addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
                        var address : Address = addresses!!.get(0)
                        countryCode =  address.getCountryCode()

                        val intent = Intent(this, AuthActivity::class.java)
                        intent.putExtra("countryCode", countryCode)
                        startActivity(intent)
                        finish()
                    }

                } catch (e : IOException) {
                    e.printStackTrace();
                }

            }
        }

    }*/

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled)
            showAlert()
        return isLocationEnabled
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.enable_location))
            .setMessage(resources.getString(R.string.location_setting_msg))
            .setPositiveButton(resources.getString(R.string.location_settings)) { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            //.setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        //start finger print authentication
        mFingerPrintAuthHelper!!.startAuth()

        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "")?.length !== 0)
        {
            if(sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("English"))
            {
                DialogBox.closeDialogE()
                val locale = Locale("en")
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            else if (sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("Chinese"))
            {
                val locale = Locale("zh")
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)

            }

            if (sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "")?.length !== 0)
            {
                Handler().postDelayed({

                    if (!sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)!!)
                    {
                        if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                        {
                            startActivity(Intent(this, SecurePinLoginActivity::class.java))
                            finish()
                        }
                        else
                        {
                            startActivity(Intent(this, BaseDashboardActivity::class.java))
                            finish()
                        }
                    }
                    else
                    {
                        DialogBox.showSimpleMessageNotCancelable(this@SplashActivity,
                            resources.getString(R.string.touch_id_as_authentication))
                    }

                }, 3000)

            }
            else
            {
                getLastLocation()
            }
        }
        else
        {
            DialogBox.languageChange(this, "", null, this@SplashActivity)
        }


    }

    override fun onPause() {
        super.onPause()
        mFingerPrintAuthHelper!!.stopAuth()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation()
    {
        if (checkPermissions())
        {
            if (isLocationEnabled())
            {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null)
                    {
                        requestNewLocationData()
                    }
                    else
                    {
                        loc(location)
                    }
                }
            }
            else
            {
                showAlert()
            }
        }
        else
        {
            requestPermissions()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData()
    {

        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult)
        {
            var mLastLocation: Location = locationResult.lastLocation
            loc(mLastLocation)
        }
    }

    private fun isLocationEnabled(): Boolean {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    private fun requestPermissions() { ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
        mLocationManager!!.removeUpdates(mLocationCallback)
    }*/

    private fun loc(location: Location)
    {
        try
        {
            geocoder = Geocoder(this,Locale.getDefault())
            addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 1)
            var address : Address = addresses!!.get(0)
            countryCode =  address.getCountryCode()

            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("countryCode", countryCode)
            startActivity(intent)
            finish()
        }
        catch(e : Exception)
        {
            Log.e("errror",e.message)
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("countryCode", countryCode)
            startActivity(intent)
            finish()
        }


    }

    override fun onNoFingerPrintHardwareFound() {

        Log.e("SplashScreen", "onNoFingerPrintHardwareFound: ")
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }


    override fun onNoFingerPrintRegistered() {

        Log.e("SplashScreen", "onNoFingerPrintRegistered: ")

        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }

    override fun onBelowMarshmallow() {
        Log.e("SplashScreen","onBelowMarshmallow")
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }

    override fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?) {

        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "")?.length !== 0)
        {
            sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, true)
            sharedPreferenceUtil.save()

            val mainIntent = Intent(this@SplashActivity, BaseDashboardActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        else
        {
            val mainIntent = Intent(this@SplashActivity, AuthActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

    override fun onAuthFailed(errorCode: Int, errorMessage: String?) {
        when (errorCode)
        {
            AuthErrorCodes.CANNOT_RECOGNIZE_ERROR ->
            {
                // mAuthMsgTv.setText("Cannot recognize your finger print. Please try again.");
                Snackbar.make(rl_main!!,resources.getString(R.string.cannot_recognise_yorfinger),Snackbar.LENGTH_LONG).show()
                count++
                if (count == 3) {
                    val intent = Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD)
                    startActivityForResult(intent, 0)
                }
            }
            AuthErrorCodes.NON_RECOVERABLE_ERROR ->
            {
            }
            AuthErrorCodes.RECOVERABLE_ERROR ->
            {
            }
        }
    }


    override fun setLanguage(radio_eng: RadioButton, radio_chin: RadioButton)
    {
        if (radio_eng.isChecked)
        {
            val locale = Locale("en")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)

            sharedPreferenceUtil.setString(SharedPreferencesConstants.LANGUAGE, "English")
            sharedPreferenceUtil.save()

            gotoDashboard()

        }
        else if (radio_chin.isChecked)
        {
            /*val locale = Locale("zh")
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)*/

            val locale = Locale("zh")
            Locale.setDefault(locale)
            val configuration: Configuration = resources.getConfiguration()
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            createConfigurationContext(configuration)

            sharedPreferenceUtil.setString(SharedPreferencesConstants.LANGUAGE, "Chinese")
            sharedPreferenceUtil.save()

            gotoDashboard()
        }
    }

    private fun gotoDashboard()
    {
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "")?.length !== 0)
        {
            val mainIntent = Intent(this@SplashActivity, BaseDashboardActivity::class.java)
            startActivity(mainIntent)
            finish()

        }
        else
        {
            getLastLocation()
        }
    }
}


//package com.example.eltwallet.activities
//
//import android.Manifest
//import android.Manifest.permission.ACCESS_COARSE_LOCATION
//import android.Manifest.permission.ACCESS_FINE_LOCATION
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Address
//import android.location.Geocoder
//import android.location.Location
//import android.location.LocationManager
//import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import androidx.core.app.ActivityCompat
//import com.example.eltwallet.R
//import com.example.eltwallet.utilities.SharedPreferenceUtil
//import com.example.eltwallet.utilities.SharedPreferencesConstants
//import java.io.IOException
//
//class SplashActivity : AppCompatActivity() {
//
//    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
//
//    var location: Location? = null
//    var addresses: List<Address>? = null
//    lateinit var address: Address
//    var geocoder: Geocoder? = null
//    lateinit var countryCode: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_splash)
//
//        supportActionBar?.hide()
//        sharedPreferenceUtil = SharedPreferenceUtil(this)
//
//
//        if (sharedPreferenceUtil.getString(
//                SharedPreferencesConstants.USER_TOKEN,
//                ""
//            )?.length !== 0
//        ) {
//
//            Handler().postDelayed({
//
//                startActivity(Intent(this, BaseDashboardActivity::class.java))
//                finish()
//
//            }, 3000)
//
//        } else {
//
//            val strArr = arrayOf<String>(
//                ACCESS_FINE_LOCATION,
//                ACCESS_COARSE_LOCATION
//            )
//
//            if (!hasPermissions(this, *strArr)) {
//
//                Handler().postDelayed({
//
//                    ActivityCompat.requestPermissions(this, strArr, 1)
//
//                }, 3000)
//
//            }
//            else
//            {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    Log.e("request14", "permissions not granted")
//
//                }
//                else
//                {
//                    Log.e("request15", "permissions granted")
//
//                    try {
//
//                        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//                        location = locationManager
//                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                        geocoder = Geocoder(this)
//
//
//                        addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
//                        var address : Address = addresses!!.get(0)
//                        countryCode =  address.getCountryCode()
//
//                        val intent = Intent(this, AuthActivity::class.java)
//                        intent.putExtra("countryCode", countryCode)
//                        startActivity(intent)
//                        finish()
//
//
//                    } catch (e : IOException) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//
////            Handler().postDelayed({
////                val intent = Intent(this, AuthActivity::class.java)
////                intent.putExtra("countryCode", "IN")
////                startActivity(intent)
////                finish()
////
////            }, 3000)
//
//        }
//
//    }
//
//    private fun hasPermissions(context2: Context?, vararg strArr: String): Boolean {
//        if (!(Build.VERSION.SDK_INT < 23 || context2 == null || strArr == null)) {
//            for (checkSelfPermission in strArr) {
//                if (ActivityCompat.checkSelfPermission(context2, checkSelfPermission) !== 0) {
//                    return false
//                }
//            }
//        }
//        return true
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//
//        if(requestCode == 1)
//        {
//            Log.e("request1", "request1")
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                Log.e("request12", "permissions not granted")
//
//            }
//            else
//            {
//                Log.e("request13", "permissions granted")
//
//                try {
//
//                    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//                    location = locationManager
//                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                    geocoder = Geocoder(this)
//
//
//                    addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
//                    var address : Address = addresses!!.get(0)
//                    countryCode =  address.getCountryCode()
//
//                    val intent = Intent(this, AuthActivity::class.java)
//                    intent.putExtra("countryCode", countryCode)
//                    startActivity(intent)
//                    finish()
//
//                } catch (e : IOException) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//    }
//}
