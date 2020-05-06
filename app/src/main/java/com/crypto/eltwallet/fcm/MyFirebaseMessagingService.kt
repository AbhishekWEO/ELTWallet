package com.crypto.eltwallet.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.BaseDashboardActivity
import com.crypto.eltwallet.activities.SplashActivity
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService()
{
    private val TAG = "MyFirebaseMsgService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)



        Log.d(TAG, "From: " + remoteMessage.from)

        var sharedPreferenceUtil: SharedPreferenceUtil = SharedPreferenceUtil(applicationContext)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0)
        {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.isFcmEnabled, true)!!)
            {
                sendNotification(remoteMessage.data.toString())
            }
            if (  /*Check if data needs to be processed by long running job*/ true)
            {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //   scheduleJob();
            }
            else
            {
                // Handle message within 10 seconds
                //   handleNow();
            }
        }


        // Check if message contains a notification payload.
        if (remoteMessage.notification != null)
        {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)

            if (sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.isFcmEnabled, true)!!)
            {
                sendNotification(remoteMessage.notification!!.body!!)
            }
        }

    }
    // [END receive_message]

    // [START on_new_token]
    override fun onNewToken(token: String)
    {
        super.onNewToken(token)
        Log.e(TAG, "Refreshed token:"+token)

        //var sharedPreferenceUtil = SharedPreferenceUtil(applicationContext)

        //sharedPreferenceUtil.setString(SharedPreferencesConstants.fcmtoken, token)
        //sharedPreferenceUtil.save()
    }
    // [END on_new_token]

    fun sendNotification(messageBody:String)
    {
        val CHANNEL_ID = "my_channel_01"
        val name: CharSequence = "my_channel"
        val Description = "This is my channel"

        val NOTIFICATION_ID = 234

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(true)
            notificationManager?.createNotificationChannel(mChannel)
        }

        val resultIntent = Intent(this, BaseDashboardActivity::class.java)
        resultIntent.putExtra("type", "notification")

        /*val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)

        stackBuilder.addParentStack(SplashActivity::class.java)

        stackBuilder.addNextIntent(resultIntent)

        val resultPendingIntent: PendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)*/
        //
        var notification_id=getRandomNumber(1,1000)
        val resultPendingIntent: PendingIntent = PendingIntent.getActivity(this, notification_id , resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        ///
        val builder  = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ELTWallet")
            .setContentText(messageBody) // // .setStyle(new NotificationCompat.BigTextStyle().bigText("Notice that the NotificationCompat.Builder constructor requires that you provide a channel ID. This is required for compatibility with Android 8.0 (API level 26) and higher, but is ignored by older versions By default, the notification's text content is truncated to fit one line. If you want your notification to be longer, you can enable an expandable notification by adding a style template with setStyle(). For example, the following code creates a larger text area"))
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setColor(resources.getColor(R.color.colorPrimaryDark))
        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }
}
