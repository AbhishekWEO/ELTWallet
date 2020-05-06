package com.crypto.eltwallet.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.crypto.eltwallet.R
import com.crypto.eltwallet.fingerPrintsInterface.FingerPrintAuthCallback
import com.crypto.eltwallet.utilities.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.ExecutionException


open class BaseActivity : AppCompatActivity(), FingerPrintAuthCallback
{
    private var mFingerPrintAuthHelper: FingerPrintAuthHelper? = null
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    private val TAG: String = BaseActivity::class.java.getSimpleName()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this)
    }

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
            foregroud = ForegroundCheckTask().execute(this).get()
            Log.e(TAG, "wellonCreate: "+foregroud)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        Log.e(TAG, "well ")
        mFingerPrintAuthHelper!!.startAuth()
        if (!sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.isBackground, false)!!)
        {
            if (!sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)!!)
            {
                if (sharedPreferenceUtil.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                {
                    var intent = Intent(this,SecurePinLoginActivity::class.java)

                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        /*sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, false)
        sharedPreferenceUtil.save()*/
    }

    override fun onResume() {
        super.onResume()
        mFingerPrintAuthHelper!!.startAuth()
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
            foregroud = ForegroundCheckTask().execute(this).get()
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
                foregroud = ForegroundCheckTask().execute(this@BaseActivity).get()
                Log.e(TAG, "onStop: $foregroud")
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
            sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, true)
            sharedPreferenceUtil.save()
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
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }


    }
}