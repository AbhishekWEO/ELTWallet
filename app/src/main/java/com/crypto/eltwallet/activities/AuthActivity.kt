package com.crypto.eltwallet.activities

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.crypto.eltwallet.R
import com.crypto.eltwallet.adapter.CustomPagerAdapter

class AuthActivity : AppCompatActivity() {

    lateinit var viewPager : ViewPager
    lateinit var roundedSolid: RelativeLayout
    lateinit var bar: RelativeLayout
    private var isLogin = true
    private val mCurrentSelectedScreen = 0
    private var mNextSelectedScreen = 0
    lateinit var txtsignup: TextView
    lateinit var txtlogin: TextView
    private var distance: Float = 0F

    var location: Location? = null
    var addresses: List<Address>? = null
    var geocoder: Geocoder? = null
    var countryCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        supportActionBar?.hide()


//        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ),
//                101
//            )
//        }
//        location = locationManager
//            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        geocoder = Geocoder(this)


        val intent = intent
        if (intent != null) {
            if (intent.getStringExtra("countryCode")!=null)
            {
                countryCode = intent.getStringExtra("countryCode")
            }

        }


        roundedSolid = findViewById(R.id.roundedsolid)
        txtsignup = findViewById(R.id.txtsignup)
        txtlogin = findViewById(R.id.txtlogin)
        bar = findViewById(R.id.bar)

        bar.setOnClickListener{
            if (isLogin) {
                Handler().post {
                    //  mPager.setCurrentItem(2); //Where "2" is the position you want to go
                    viewPager.currentItem = getItem(+1) //getItem(-1) for previous
                }


            } else {
                Handler().post {
                    //  mPager.setCurrentItem(2); //Where "2" is the position you want to go
                    viewPager.currentItem = getItem(-1) //getItem(-1) for previous
                }
            }
        }

        distance = resources.getDimensionPixelSize(R.dimen.distance).toFloat()

        Log.e("countryCode2", countryCode)

        viewPager = findViewById<View>(R.id.pager) as ViewPager
        viewPager.adapter = CustomPagerAdapter(this, countryCode)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                if (position == mCurrentSelectedScreen) {
                    // We are moving to next screen on right side
                    if (positionOffset > 0.5) {
                        // Closer to next screen than to current
                        if (position + 1 != mNextSelectedScreen) {
                            mNextSelectedScreen = position + 1
                            //  updateStaticViewsForScreen( mNextSelectedScreen );
                            barmovementController()
                        }
                    } else {
                        // Closer to current screen than to next
                        if (position != mNextSelectedScreen) {
                            mNextSelectedScreen = position
                            //updateStaticViewsForScreen( mNextSelectedScreen );
                            barmovementController()

                        }
                    }
                } else {
                    // We are moving to next screen left side
                    if (positionOffset > 0.5) {
                        // Closer to current screen than to next
                        if (position + 1 != mNextSelectedScreen) {
                            mNextSelectedScreen = position + 1
                            //    updateStaticViewsForScreen( mNextSelectedScreen );
                            barmovementController()
                        }
                    } else {
                        // Closer to next screen than to current
                        if (position != mNextSelectedScreen) {
                            mNextSelectedScreen = position
                            //updateStaticViewsForScreen( mNextSelectedScreen );
                            barmovementController()
                        }
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {

            }

        })

    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    private fun barmovementController() {
        if (isLogin) {
            roundedSolid.animate().translationX(distance)
            isLogin = false
            txtsignup.setTextColor(resources.getColor(R.color.white))
            txtlogin.setTextColor(resources.getColor(R.color.skyblue))
            viewPager.arrowScroll(ViewPager.FOCUS_RIGHT)
        } else {
            roundedSolid.animate().translationX(0f)
            isLogin = true
            txtlogin.setTextColor(resources.getColor(R.color.white))
            txtsignup.setTextColor(resources.getColor(R.color.skyblue))
            viewPager.arrowScroll(ViewPager.FOCUS_LEFT)
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//
//        if(requestCode == 101)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return
//                }
//                try {
//                    addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
//                    var address : Address = addresses!!.get(0)
//                    countryCode =  address.getCountryCode()
//                    Toast.makeText(this, countryCode, Toast.LENGTH_SHORT).show()
//
//                } catch (e : IOException) {
//                    e.printStackTrace();
//                }
//            } else {
//                //not granted
//            }
//
//        }
//        else
//        {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//
//        if(requestCode == 101)
//        {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return
//                }
//                try {
//                    addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
//                    var address : Address = addresses!!.get(0)
//                    countryCode =  address.getCountryCode()
//                    Toast.makeText(this, countryCode, Toast.LENGTH_SHORT).show()
//
//                } catch (e : IOException) {
//                    e.printStackTrace();
//                }
//            } else {
//                //not granted
//            }
//
//        }
//        else
//        {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

//    override fun onResume() {
//        super.onResume();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        try {
//            addresses = geocoder?.getFromLocation(location!!.getLatitude(), location!!.getLongitude(), 10)
//            var address : Address = addresses!!.get(0)
//            countryCode =  address.getCountryCode()
//            Toast.makeText(this, countryCode, Toast.LENGTH_SHORT).show()
//        } catch (e : IOException) {
//            e.printStackTrace();
//        }
//    }


}
