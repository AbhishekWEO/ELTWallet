package com.crypto.eltwallet.utilities

import android.Manifest
import android.R
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class UtilityPermissions
{
    companion object
    {
        val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun checkPermissionn(context: Context?): Boolean
        {
            val currentAPIVersion = Build.VERSION.SDK_INT
            return if (currentAPIVersion >= Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        val alertBuilder =
                            AlertDialog.Builder(context)
                        alertBuilder.setCancelable(true)
                        alertBuilder.setTitle("Permission necessary")
                        alertBuilder.setMessage("External storage permission is necessary")
                        alertBuilder.setPositiveButton(R.string.yes) { dialog, which ->
                            ActivityCompat.requestPermissions(
                                (context as Activity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                        }
                        val alert = alertBuilder.create()
                        alert.show()
                    } else {
                        ActivityCompat.requestPermissions((context as Activity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    false
                } else {
                    true
                }
            } else {
                true
            }
        }

        fun checkPermission(context: Context?): Boolean
        {
            val currentAPIVersion = Build.VERSION.SDK_INT
            return if (currentAPIVersion >= Build.VERSION_CODES.M)
            {
                if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.CAMERA))
                    {
                        ActivityCompat.requestPermissions((context as Activity?)!!,
                            arrayOf(
                                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    else
                    {
                        ActivityCompat.requestPermissions((context as Activity?)!!,
                            arrayOf(
                                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                    }
                    false
                }
                else
                {
                    true
                }
            }
            else
            {
                true
            }
        }

        fun isNetworkAvailable(context: Context): Boolean
        {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        }



    }

}