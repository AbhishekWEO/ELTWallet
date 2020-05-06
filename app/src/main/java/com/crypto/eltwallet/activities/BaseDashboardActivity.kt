package com.crypto.eltwallet.activities

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.crypto.eltwallet.R
import com.crypto.eltwallet.fingerPrintsInterface.FingerPrintAuthCallback
import com.crypto.eltwallet.fragments_dashborad.*
import com.crypto.eltwallet.utilities.MEssageEvent
import com.crypto.eltwallet.utilities.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.ExecutionException

class BaseDashboardActivity : AppCompatActivity(), FingerPrintAuthCallback {

    lateinit var bottomNavigationView: BottomNavigationView
    var active_fragment: String = ""
    var type : String = "home"

    ///16-04-2020

    private var mFingerPrintAuthHelper: FingerPrintAuthHelper? = null
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    private val TAG: String = BaseDashboardActivity::class.java.getSimpleName()



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MEssageEvent?)
    {
        //Do something
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        Log.e(TAG, "onStart: ")
        val foregroud: Boolean
        try {
            foregroud = ForegroundCheckTask().execute(this@BaseDashboardActivity).get()
            Log.e(TAG, "wellonCreate: $foregroud")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        Log.e(TAG, "well ")
        mFingerPrintAuthHelper!!.startAuth()
        if (!sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.isBackground, false)!!)
        {
            if (!sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE,
                    false)!!)
            {
                if (sharedPreferenceUtil.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                {
                    startActivity(Intent(this, SecurePinLoginActivity::class.java))
                    finish()

                }
                else
                {
                }
            }
            else
            {
                DialogBox.showSimpleMessageNotCancelable(this, resources.getString(R.string.touch_id_as_authentication))
            }
        }
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, false)
        sharedPreferenceUtil.save()
    }

    override fun onResume() {
        super.onResume()
        mFingerPrintAuthHelper!!.startAuth()

        ///21-04-2020
        /*FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d("FirebaseToken", token)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })*/

        //
    }

    override fun onPause() {
        super.onPause()
        mFingerPrintAuthHelper!!.stopAuth()
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)
        Log.e(TAG, "onStop:he oye ")
        var foregroud = false
        try
        {
            foregroud = ForegroundCheckTask().execute(this@BaseDashboardActivity).get()
            Log.e(TAG, "onStop:"+foregroud)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, true)
        sharedPreferenceUtil.save()

        countDownTimer.start()
    }

    var countDownTimer: CountDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            var foregroud = false
            try {
                foregroud = ForegroundCheckTask().execute(this@BaseDashboardActivity).get()
                Log.e(TAG, "onStop:"+foregroud)
                sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, foregroud)
                sharedPreferenceUtil.save()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setappStatus(false)
    }

    fun setappStatus(aBoolean: Boolean?) {
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.InApp, aBoolean)
        sharedPreferenceUtil.save()
    }


    ////end

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_dashboard)

        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this)

        bottomNavigationView = findViewById(R.id.bottom_navigation)


        val extras = intent.extras
        if(!(extras == null))
        {
            if (intent.getStringExtra("type") == "wallet")
            {
                bottomNavigationView.selectedItemId = R.id.action_wallet

                FragmentUtils.addFragmentsInBarContainer(WalletFragment(), supportFragmentManager)
                active_fragment = "wallet"
            }
            else if(intent.getStringExtra("type").equals("notification"))
            {
                bottomNavigationView.selectedItemId = R.id.action_notifications
                FragmentUtils.addFragmentsInBarContainer(NotificationsFragment(), supportFragmentManager)

                active_fragment = "notifications"
            }
        }
        else
        {
            FragmentUtils.addFragmentsInBarContainer(HomeFragment(), supportFragmentManager)
            active_fragment = "home"

        }

//            FragmentUtils.addFragmentsInBarContainer(
//                HomeFragment(),
//                supportFragmentManager
//            )
//
//            active_fragment = "home"

        bottomNavigationView.setItemIconTintList(null)

        val menuView = bottomNavigationView.getChildAt(0) as? ViewGroup ?: return
        menuView.forEach {
            findViewById<View>(R.id.action_notifications)?.setPadding(0, 0, 0, 0)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.action_home -> {

                            if (!(active_fragment.equals("home"))) {
                                FragmentUtils.addFragmentsInBarContainer(
                                    HomeFragment(),
                                    supportFragmentManager
                                )
                            }
                            active_fragment = "home"

                        }

                        R.id.action_wallet -> {

                            if (!(active_fragment.equals("wallet"))) {
                                FragmentUtils.addFragmentsInBarContainer(
                                    WalletFragment(),
                                    supportFragmentManager
                                )
                            }
                            active_fragment = "wallet"

                        }

                        R.id.action_news -> {

                            if (!(active_fragment.equals("news")))
                            {
                                FragmentUtils.addFragmentsInBarContainer(NewsFragment(), supportFragmentManager)
                            }
                            active_fragment = "news"

                        }

                        R.id.action_notifications -> {

                            if (!(active_fragment.equals("notifications"))) {
                                FragmentUtils.addFragmentsInBarContainer(NotificationsFragment(), supportFragmentManager)
                            }
                            active_fragment = "notifications"

                        }

                        R.id.action_settings -> {
                            if (!(active_fragment.equals("settings"))) {
                                FragmentUtils.addFragmentsInBarContainer(
                                    SettingsFragment(),
                                    supportFragmentManager
                                )
                            }
                            active_fragment = "settings"
                        }
                    }
                    return true
                }
            })

    }

    internal var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        //Checking for fragment count on backstack
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
            return
        }
    }

    //////16-04-2020

    override fun onNoFingerPrintHardwareFound() {
        Log.e("BaseDashboardActivity", "onNoFingerPrintHardwareFound: ")
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }

    override fun onNoFingerPrintRegistered() {
        Log.e("BaseDashboardActivity", "onNoFingerPrintHardwareFound: ")
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }

    override fun onBelowMarshmallow() {
        Log.e(TAG, "onBelowMarshmallow")
        sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil.save()
    }

    override fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?) {
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "")?.length !== 0)
        {
            DialogBox.closeDialogE()
        }
        else
        {
            val mainIntent = Intent(this, AuthActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

    override fun onAuthFailed(errorCode: Int, errorMessage: String?) {
        when (errorCode)
        {
            AuthErrorCodes.CANNOT_RECOGNIZE_ERROR ->
            {
                Toast.makeText(this,resources.getString(R.string.cannot_recognise_yorfinger), Toast.LENGTH_LONG).show()
            }
            AuthErrorCodes.NON_RECOVERABLE_ERROR ->
            {
            }
            AuthErrorCodes.RECOVERABLE_ERROR ->
            {
            }
        }
    }


    ///

    class ForegroundCheckTask : AsyncTask<Context?, Void?, Boolean>()
    {
        override fun doInBackground(vararg params: Context?): Boolean {
            val context = params[0]!!.applicationContext
            return isAppOnForeground(context)
        }

        private fun isAppOnForeground(context: Context): Boolean
        {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses)
            {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }


    }
}
