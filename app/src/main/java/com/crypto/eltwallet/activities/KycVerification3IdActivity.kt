package com.crypto.eltwallet.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.adapter.KycDocTypeAdapter
import com.crypto.eltwallet.interfaces.DocsIdInterface
import com.crypto.eltwallet.model.GetKycDocTypesModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.CountryPickerListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class KycVerification3IdActivity : AppCompatActivity(), View.OnClickListener,
    CountryPickerListener,DocsIdInterface {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: RelativeLayout
    var context : Context = this@KycVerification3IdActivity

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth: String

    lateinit var recycler_view: RecyclerView
    private val arrayList = ArrayList<GetKycDocTypesModel.DataBean>()

    var tv_changeCountry : TextView?=null

    private var countryPicker: CountryPicker? = null
    private var click: ClickableSpan?=null
    private var legal_name : String=""
    public var country_name : String=""

    var location: Location? = null
    var addresses: List<Address>? = null
    lateinit var address: Address
    var geocoder: Geocoder? = null
    lateinit var countryCode: String
    private var isLocationEnabled: Boolean = false
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var mLocationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_verification3_id)

        supportActionBar?.hide()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tv_changeCountry = findViewById(R.id.tv_changeCountry)
        countryPicker = CountryPicker()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        input_auth =
            sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()



        recycler_view = findViewById(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(context)

        getSetData()

        attemptToGetKycDocTypes()

        setOnClickListener()
    }

    private fun getSetData()
    {
        legal_name = intent.getStringExtra(ConstantsRequest.LEGAL_NAME)
        country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)

    }

    private fun setOnClickListener()
    {
        countryPicker!!.setListener(this)
        tv_changeCountry!!.setOnClickListener(this)
    }



    fun attemptToGetKycDocTypes()
    {
        DialogBox.showLoader(this)
        (RetrofitClient.getClient.attemptToGetKycDocTypes(input_auth).enqueue(object :
            Callback<GetKycDocTypesModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<GetKycDocTypesModel>,
                response: Response<GetKycDocTypesModel>
            ) {
                DialogBox.closeDialogE()
                arrayList.clear()

                var size : Int = response.body()!!.getKycDoc()!!.size

                if(size == 0) {

                    recycler_view.visibility = View.GONE
                }
                else
                {

                    recycler_view.visibility = View.VISIBLE

                    for (i in 0 until size) {

                        var getKycDocTypesModel = GetKycDocTypesModel.DataBean()

                        getKycDocTypesModel.id = response.body()!!.getKycDoc()?.get(i)?.id
                        getKycDocTypesModel.name = response.body()!!.getKycDoc()?.get(i)?.name
                        getKycDocTypesModel.doc_msg = response.body()!!.getKycDoc()?.get(i)?.doc_msg
                        getKycDocTypesModel.no_of_attachment = response.body()!!.getKycDoc()?.get(i)?.no_of_attachment
                        getKycDocTypesModel.doc_icon = response.body()!!.getKycDoc()?.get(i)?.doc_icon

                        arrayList.add(getKycDocTypesModel)

                    }

                    recycler_view.adapter = KycDocTypeAdapter(arrayList, context,this@KycVerification3IdActivity)
                }

            }

            override fun onFailure(call: Call<GetKycDocTypesModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }
    override fun onClick(v: View?) {
        when (v!!.id)
        {
            R.id.tv_changeCountry->
            {
                countryPicker!!.show(this@KycVerification3IdActivity.getSupportFragmentManager(), "well")
            }
        }
    }

    override fun onSelectCountry(s: String?, s1: String?, s2: String?, i: Int)
    {
        Log.e("taggss", "onSelectCountry:"+s+" "+s1+" "+s2+",position "+i)

        val builder = SpannableStringBuilder()

        val styleSpanBold = StyleSpan(Typeface.BOLD)
        country_name = s!!
        //val countryName = s
        val countryNameSpannable = SpannableString(country_name)
        countryNameSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, country_name!!.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        countryNameSpannable.setSpan(styleSpanBold, 0, country_name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        builder.append(countryNameSpannable)

        val has_been_selected = " "+resources.getString(R.string.has_been_selected)
        val selectedSpannable = SpannableString(has_been_selected)
        selectedSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, has_been_selected.length, 0)
        builder.append(selectedSpannable)

        val changeCountry = " "+resources.getString(R.string.change_country)
        val changeCountrySpannable = SpannableString(changeCountry)
        changeCountrySpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, changeCountry.length, 0)
        changeCountrySpannable.setSpan(UnderlineSpan(), 0, changeCountry.length, 0)
        changeCountrySpannable.setSpan(styleSpanBold, 0, changeCountry.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        //changeCountrySpannable.setSpan(click, 0, changeCountrySpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(changeCountrySpannable)

        //tv_changeCountry!!.setText(s+" "+resources.getString(R.string.has_been_selected)+" "+resources.getString(R.string.change_country))

        tv_changeCountry!!.setText(builder, TextView.BufferType.SPANNABLE)

        countryPicker!!.dismiss()

    }

    override fun docsId(docs_id: String, docs_name: String)
    {
        Log.e("doc_id",docs_id)
        //var intent = Intent(this@KycVerification3IdActivity,UploadDocumentActivity::class.java)
        var intent = Intent(this@KycVerification3IdActivity,ScanDocsActivity::class.java)
        intent.putExtra("docside",resources.getString(R.string.front_side))
        intent.putExtra("docs_name",docs_name)
        intent.putExtra(ConstantsRequest.DOC_ID,docs_id)
        intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
        intent.putExtra(ConstantsRequest.LEGAL_NAME,legal_name)

        startActivity(intent)

    }

    override fun onResume() {
        super.onResume()

        if (country_name.isEmpty() || country_name==null)
        {
            getLastLocation()
        }
        else
        {
            setCountry()
        }

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


    private fun loc(location: Location)
    {
        geocoder = Geocoder(this,Locale.getDefault())
        addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 1)
        var address : Address = addresses!!.get(0)
        countryCode =  address.getCountryCode()

        country_name = address.countryName
        Log.e("country_name",country_name)

        setCountry()

    }

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

    private fun setCountry()
    {
        val builder = SpannableStringBuilder()

        val styleSpanBold = StyleSpan(Typeface.BOLD)

        val countryNameSpannable = SpannableString(country_name)
        countryNameSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, country_name!!.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        countryNameSpannable.setSpan(styleSpanBold, 0, country_name.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        builder.append(countryNameSpannable)

        val has_been_selected = " "+resources.getString(R.string.has_been_selected)
        val selectedSpannable = SpannableString(has_been_selected)
        selectedSpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, has_been_selected.length, 0)
        builder.append(selectedSpannable)

        val changeCountry = " "+resources.getString(R.string.change_country)
        val changeCountrySpannable = SpannableString(changeCountry)
        changeCountrySpannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, changeCountry.length, 0)
        changeCountrySpannable.setSpan(UnderlineSpan(), 0, changeCountry.length, 0)
        changeCountrySpannable.setSpan(styleSpanBold, 0, changeCountry.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        builder.append(changeCountrySpannable)

        tv_changeCountry!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

}