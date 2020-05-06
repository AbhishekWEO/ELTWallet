package com.crypto.eltwallet.utilities

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.*
import com.crypto.eltwallet.adapter.NotificationsAdapter
import com.crypto.eltwallet.interfaces.LanguageChange
import com.crypto.eltwallet.model.DeleteWalletModel
import com.crypto.eltwallet.model.NotificationClearModel
import com.crypto.eltwallet.model.NotificationsModel
import com.crypto.eltwallet.model.SendEltModel
import com.crypto.eltwallet.network.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.hbb20.CountryCodePicker
import com.journeyapps.barcodescanner.BarcodeEncoder
import de.mateware.snacky.Snacky
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DialogBox {

    companion object {

        lateinit var dialogE: Dialog
        lateinit var login_dialog: Dialog

        fun showMessage(context: Context, message: String) {

            var message1 = message

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_error)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val text = dialogE.findViewById(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = message1
            val dialogButton = dialogE.findViewById(R.id.dialogButtonOK) as Button
            dialogButton.setOnClickListener {
                dialogE.dismiss()

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun showError(context: Activity, message: String)
        {
            var message1 = message

            try {
                val jsonObject = JSONObject(message)
                message1 = jsonObject.getString("message")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_error)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val text = dialogE.findViewById(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = message1
            val dialogButton = dialogE.findViewById(R.id.dialogButtonOK) as Button
            dialogButton.setOnClickListener {
                dialogE.dismiss()

                if (message1.equals("Your session is no longer valid. Please sign in again."))
                {
                    var sharedPreferenceUtil = SharedPreferenceUtil(context)
                    sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,"")
                    sharedPreferenceUtil.save()

                    sharedPreferenceUtil.clearAll()
                    context.startActivity(Intent(context, SplashActivity::class.java))
                    context.finish()
                }
                else if (message1.equals("Your session is no longer valid. Please sign in again"))
                {
                    var sharedPreferenceUtil = SharedPreferenceUtil(context)
                    sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,"")
                    sharedPreferenceUtil.save()

                    sharedPreferenceUtil.clearAll()
                    context.startActivity(Intent(context, SplashActivity::class.java))
                    context.finish()
                }
                else if (message1.equals("Authorization failed, Please login again"))
                {
                    var sharedPreferenceUtil = SharedPreferenceUtil(context)
                    sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,"")
                    sharedPreferenceUtil.save()

                    sharedPreferenceUtil.clearAll()
                    context.startActivity(Intent(context, SplashActivity::class.java))
                    context.finish()
                }

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun showError(context: Context, message: String) {

            var message1 = message

            try {
                val jsonObject = JSONObject(message)
                message1 = jsonObject.getString("message")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_error)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val text = dialogE.findViewById(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = message1
            val dialogButton = dialogE.findViewById(R.id.dialogButtonOK) as Button
            dialogButton.setOnClickListener {
                dialogE.dismiss()


            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun showForgot(context: Context, message: String) {

            var message1 = message

            try {
                val jsonObject = JSONObject(message)
                message1 = jsonObject.getString("message")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_error)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val text = dialogE.findViewById(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = message1
            val dialogButton = dialogE.findViewById(R.id.dialogButtonOK) as Button
            dialogButton.setOnClickListener {
                val intent = Intent(context, AuthActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun showWalletDelete(context: Context, input_id : String, input_auth : String, linearLayout : LinearLayout) {

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_clear_wallet)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            val no = dialogE.findViewById(R.id.no) as Button
            no.setOnClickListener {
                dialogE.dismiss()
            }

            val yes = dialogE.findViewById(R.id.yes) as Button
            yes.setOnClickListener {

                attemptToDeleteWallet(context, input_id, input_auth, linearLayout)

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun attemptToDeleteWallet(context: Context, input_id: String, input_auth: String, linearLayout : LinearLayout) {
            showLoader(context)
            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.WALLET_ID, input_id)

            (RetrofitClient.getClient.attemptToDeleteWallet(input_auth, weakHashMap).enqueue(object :
                Callback<DeleteWalletModel> {
                @RequiresApi(api = 16)
                override fun onResponse(
                    call: Call<DeleteWalletModel>,
                    response: Response<DeleteWalletModel>
                ) {
                    closeDialogE()
                    if(response == null)
                    {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        showError(context, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        showError(context, (response.body() as DeleteWalletModel).message)
                    }
                    else if(response.code() == 200)
                    {
                        dialogE.dismiss()
                        (context as Activity).finish()
                    }
                }

                override fun onFailure(call: Call<DeleteWalletModel>, th: Throwable) {
                    closeDialogE()
                    Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

                }
            }))
        }

        fun showReceive(context: Context, input_walletAddress: String) {

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_receive)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val walletAddress = dialogE.findViewById(R.id.walletAddress) as TextView
            walletAddress.text = input_walletAddress

            val qr_image = dialogE.findViewById(R.id.qr_image) as ImageView
            val multiFormatWriter = MultiFormatWriter()
            try {
                val bitMatrix =
                    multiFormatWriter.encode(input_walletAddress, BarcodeFormat.QR_CODE, 250, 250)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                qr_image.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val copy_address = dialogE.findViewById(R.id.copy_address) as TextView
            copy_address.setOnClickListener {

                val textToCopy = input_walletAddress
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, context.resources.getString(R.string.wallet_copied), Toast.LENGTH_LONG).show()

            }

            val close = dialogE.findViewById(R.id.close) as TextView
            close.setOnClickListener {
                dialogE.dismiss()
            }

            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun showSendEltOtp(context: Context, input_auth: String, linearLayout: RelativeLayout) {

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_send_elt_otp)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            val cancel = dialogE.findViewById(R.id.cancel) as Button
            cancel.setOnClickListener {
                dialogE.dismiss()
            }

            val otpTextView = dialogE.findViewById(R.id.otp_view) as OtpTextView
            otpTextView?.requestFocusOTP()
            otpTextView?.otpListener = object : OTPListener {
                override fun onInteractionListener() {

                }

                override fun onOTPComplete(otp: String) {

                    attemptToSendElt(context, otp, input_auth, linearLayout)

                }
            }

            val save = dialogE.findViewById(R.id.save) as Button
            save.setOnClickListener {


            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }


        fun attemptToSendElt(context: Context, otp : String, input_auth : String, linearLayout: RelativeLayout) {
            showLoader(context)
            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.PASSCODE, otp)

            (RetrofitClient.getClient.attemptToSendElt(input_auth, weakHashMap).enqueue(object :
                Callback<SendEltModel> {
                @RequiresApi(api = 16)
                override fun onResponse(
                    call: Call<SendEltModel>,
                    response: Response<SendEltModel>
                ) {
                    closeDialogE()
                    if (response == null) {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    } else if (response.code() == 401) {
                        showError(context, (response.errorBody()!!.string()).toString())
                    } else if (response.code() == 202) {
                        showError(context, (response.body() as SendEltModel).message)
                    } else if (response.code() == 200) {

                        Toast.makeText(context, R.string.successfully_sent, Toast.LENGTH_SHORT).show()
                        (context as Activity).finish()

                    }

                }

                override fun onFailure(call: Call<SendEltModel>, th: Throwable) {
                    closeDialogE()
                    Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                        .show()

                }
            }))
        }

        fun showNotificationClear(context: Context, input_id : String, pos : Int, input_auth : String, linearLayout : RelativeLayout, arrayList : ArrayList<NotificationsModel.DataBean>, notificationsAdapter : NotificationsAdapter) {

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_clear_notification)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            val no = dialogE.findViewById(R.id.no) as Button
            no.setOnClickListener {
                dialogE.dismiss()
            }

            val yes = dialogE.findViewById(R.id.yes) as Button
            yes.setOnClickListener {

                dialogE.dismiss()

                arrayList.removeAt(pos)
                notificationsAdapter.notifyDataSetChanged()


                attemptToClearNotification(context, input_id, input_auth, linearLayout)

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        fun attemptToClearNotification(context: Context, input_id: String, input_auth: String, linearLayout : RelativeLayout) {

            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.NOTIFICATION_ID, input_id)

            (RetrofitClient.getClient.attemptToClearNotification(input_auth, weakHashMap).enqueue(object :
                Callback<NotificationClearModel> {
                @RequiresApi(api = 16)
                override fun onResponse(
                    call: Call<NotificationClearModel>,
                    response: Response<NotificationClearModel>
                ) {

                    if(response == null)
                    {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        showError(context, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        showError(context, (response.body() as DeleteWalletModel).message)
                    }
                    else if(response.code() == 200)
                    {
                        Log.e("Success", "Success")
                    }
                }

                override fun onFailure(call: Call<NotificationClearModel>, th: Throwable) {

                    Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

                }
            }))
        }


        fun showNotificationFailedMessage(context: Context, transaction_response: String, address_from: String, address_to: String, amt: String) {

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_failed_notification)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = transaction_response

            val no = dialogE.findViewById(R.id.no) as Button
            no.setOnClickListener {
                dialogE.dismiss()
            }

            val yes = dialogE.findViewById(R.id.yes) as Button
            yes.setOnClickListener {

                dialogE.dismiss()

                val intent = Intent(context, SendActivity::class.java)
                intent.putExtra(ConstantsRequest.WALLET_ADDRESS, address_from)
                intent.putExtra(ConstantsRequest.TO, address_to)
                intent.putExtra(ConstantsRequest.AMOUNT, amt)
                context.startActivity(intent)


            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }



        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null)
            {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(activity: Activity, editText: View) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null)
            {
                view = View(activity)
            }
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            editText.requestFocus()
        }

        fun showSimpleMessageNotCancelable(context: Context, message: String?)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setCancelable(false)
            dialogE.setContentView(R.layout.message_dialog)
            // set the custom dialog components - text, image and button
            val text =
                dialogE.findViewById<View>(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 =
                dialogE.findViewById<View>(R.id.text1) as TextView
            text1.text = message

            if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        //

        fun showUpdateEmail(context: Context, user_token: String?)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setCancelable(false)
            dialogE.setContentView(R.layout.update_email)

            /*var lp =  WindowManager.LayoutParams()
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialogE.getWindow().setAttributes(lp)*/

            val rl_main = dialogE.findViewById<View>(R.id.rl_main) as RelativeLayout
            val edt_email = dialogE.findViewById<View>(R.id.edt_email) as EditText

            val img_close = dialogE.findViewById<View>(R.id.img_close) as ImageView
            val tv_update = dialogE.findViewById<View>(R.id.tv_update) as TextView
            val progressBarUpdate = dialogE.findViewById<View>(R.id.progressBarUpdate) as ProgressBar

            img_close.setOnClickListener {
                dialogE.dismiss()
            }

            tv_update.setOnClickListener {

                if (edt_email.text.toString().trim().equals(""))
                {
                    //Snackbar.make(rl_main, R.string.enter_your_email, Snackbar.LENGTH_LONG).show()
                    Snacky.builder()
                        .setActivity(context as Activity?)
                        .setText(R.string.enter_your_email)
                        .setBackgroundColor(context.getResources().getColor(R.color.skyblue))
                        .setDuration(Snacky.LENGTH_SHORT)
                        .success()
                        .show()

                }
                else if (!edt_email.text.toString().trim().matches(Constants.EMAIL_PATREN.toRegex()))
                {

                    //Snackbar.make(rl_main, R.string.enter_your_correct_email, Snackbar.LENGTH_LONG).show()
                    Snacky.builder()
                        .setActivity(context as Activity?)
                        .setText(R.string.enter_your_correct_email)
                        .setBackgroundColor(context.getResources().getColor(R.color.skyblue))
                        .setDuration(Snacky.LENGTH_SHORT)
                        .success()
                        .show()
                    edt_email.setText("")
                }
                else
                {

                    (context as IDVerificationActivity).updateEmail(img_close,progressBarUpdate,tv_update,edt_email.text.toString().trim(),
                        dialogE)

                }
            }

            if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        //

        fun showUpdateEmail2(context: Context, user_token: String?, email: String?)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setCancelable(false)
            dialogE.setContentView(R.layout.update_email2)


            val rl_main = dialogE.findViewById<View>(R.id.rl_main) as RelativeLayout
            val img_close = dialogE.findViewById<View>(R.id.img_close) as ImageView
            val tv_resend = dialogE.findViewById<View>(R.id.tv_resend) as TextView
            val tv_update = dialogE.findViewById<View>(R.id.tv_update) as TextView
            val progressBarUpdate = dialogE.findViewById<View>(R.id.progressBarUpdate) as ProgressBar

            tv_resend.setOnClickListener {
                //dialogE.dismiss()
                (context as IDVerificationActivity)
                    .updateEmail(img_close,progressBarUpdate,tv_resend,email!!, dialogE)
            }

            tv_update.setOnClickListener {
                dialogE.dismiss()
                showUpdateEmail(context,user_token)
            }

            if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        fun updateMobile(context:Context)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window
                .setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.update_phoneno)

            //val text = dialogE.findViewById<View>(R.id.text) as TextView
            //text.text = "Update your contact number"
            val ccp: CountryCodePicker = dialogE.findViewById(R.id.ccp)
            ccp.setFlagSize(0)
            ccp.showNameCode(false)
            val edt_mobile = dialogE.findViewById<View>(R.id.edt_mobile) as EditText
            val img_close = dialogE.findViewById<View>(R.id.img_close) as ImageView
            val tv_update = dialogE.findViewById<View>(R.id.tv_update) as TextView
            val progressBarUpdate = dialogE.findViewById<View>(R.id.progressBarUpdate) as ProgressBar

            img_close.setOnClickListener{
                dialogE.dismiss()
            }

            tv_update.setOnClickListener {

                if (edt_mobile.text.toString().trim().isEmpty())
                {
                    Snacky.builder()
                        .setActivity(context as Activity)
                        .setText(R.string.enter_your_mobileno)
                        .setBackgroundColor(context.getResources().getColor(R.color.skyblue))
                        .setDuration(Snacky.LENGTH_SHORT)
                        .info()
                        .show()
                }
                else if (edt_mobile.text.trim().length < 9)
                {
                    Snacky.builder()
                        .setActivity(context as Activity)
                        .setText(R.string.enter_atleast_your_mobileno)
                        .setBackgroundColor(context.getResources().getColor(R.color.skyblue))
                        .setDuration(Snacky.LENGTH_SHORT)
                        .info()
                        .show()
                }
                else if (edt_mobile.text.trim().length != 0)
                {
                    (context as IDVerificationActivity)
                        .updatePhn(img_close,progressBarUpdate,tv_update,ccp.selectedCountryCode,edt_mobile.text.toString().trim(),
                            dialogE)

                }
                else
                {


                }
            }
            if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        //
        fun showErrorDialog(context: Activity, message: String) {

            var message1 = message

            try {
                val jsonObject = JSONObject(message)
                message1 = jsonObject.getString("message")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_error)
            dialogE.setCancelable(false)

            if (dialogE != null) {
                if (dialogE.isShowing()) {
                    return
                }
            }

            // set the custom dialog components - text, image and button
            val text = dialogE.findViewById(R.id.text) as TextView
            text.text = context.resources.getString(R.string.app_name)
            val text1 = dialogE.findViewById(R.id.text1) as TextView
            text1.text = message1
            val dialogButton = dialogE.findViewById(R.id.dialogButtonOK) as Button
            dialogButton.setOnClickListener {
                dialogE.dismiss()
                context.finish()

            }
            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }

        ////////////

        fun showUploadBackDocs(context: Context)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setCancelable(false)
            dialogE.setContentView(R.layout.back_docs)



            val tv_yes = dialogE.findViewById<View>(R.id.tv_yes) as TextView
            val tv_skip = dialogE.findViewById<View>(R.id.tv_skip) as TextView

            tv_yes.setOnClickListener {
                dialogE.dismiss()
                (context as CheckReadabilityActivity).scanDocs()
            }

            tv_skip.setOnClickListener {
                dialogE.dismiss()
                (context as CheckReadabilityActivity).uploadDocs()
            }

            if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        fun closeDialogE()
        {
            try {
                if (dialogE != null && dialogE.isShowing())
                    dialogE.dismiss();
            } catch (e:Exception) {
                e.printStackTrace();
            }
        }

        ////////////////////////////////////
        fun showLoader(context: Context)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogE.setCancelable(false)
            dialogE.setContentView(R.layout.loader_layout)

            var img_loader = dialogE.findViewById<View>(R.id.img_loader) as ImageView
            Glide.with(context).asGif().load(Integer.valueOf(R.drawable.logo2)).into(img_loader)


            //if (dialogE != null && dialogE.isShowing) return
            dialogE.show()
        }

        //
        fun showLoginLoader(context: Context)
        {
            login_dialog = Dialog(context)
            login_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            login_dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            login_dialog.setCancelable(false)
            login_dialog.setContentView(R.layout.loader_layout)

            var img_loader = login_dialog.findViewById<View>(R.id.img_loader) as ImageView
            Glide.with(context).asGif().load(Integer.valueOf(R.drawable.logo2)).into(img_loader)

            //if (dialogE != null && dialogE.isShowing) return
            //login_dialog.show()
        }
        fun showLoginLoader2()
        {
            login_dialog.show()
        }
        fun closeLoginDialog()
        {
            try {
                if (login_dialog != null && login_dialog.isShowing())
                    login_dialog.dismiss();
            } catch (e:Exception) {
                e.printStackTrace();
            }
        }

        //

        //
        fun showLangDialog(context: Context, message: String?, listener: View.OnClickListener?)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.setCancelable(false)
            dialogE.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_clear_wallet)

            val tv_title = dialogE.findViewById(R.id.text) as TextView
            val tv_msg = dialogE.findViewById(R.id.text1) as TextView
            val btn_yes = dialogE.findViewById(R.id.yes) as Button
            val btn_no = dialogE.findViewById(R.id.no) as Button

            tv_title.text = context.resources.getString(R.string.app_name)
            tv_msg.text = message

            btn_yes.setOnClickListener(listener)
            btn_no.setOnClickListener(listener)

            if (dialogE != null && dialogE.isShowing()) return
            dialogE.show()
        }
        //
        fun languageChange(context: Context, message: String, listener: View.OnClickListener?,
            languageChange: LanguageChange)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.setCancelable(false)
            dialogE.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.choose_language)
            val radio_eng: RadioButton = dialogE.findViewById(R.id.radio_eng)
            val radio_chin: RadioButton = dialogE.findViewById(R.id.radio_chin)
            val rl_ok: RelativeLayout = dialogE.findViewById(R.id.rl_ok)
            rl_ok.setOnClickListener {
                dialogE.cancel()
                languageChange.setLanguage(radio_eng, radio_chin)
            }
            if (dialogE != null && dialogE.isShowing()) return
            dialogE.show()
        }

        //logout
        fun showLogoutDialog(context: Context, listener: View.OnClickListener?)
        {
            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.setCancelable(false)
            dialogE.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.logout_layout)

            val btn_yes = dialogE.findViewById(R.id.yes) as Button
            val btn_no = dialogE.findViewById(R.id.no) as Button

            btn_yes.setOnClickListener(listener)
            btn_no.setOnClickListener(listener)

            if (dialogE != null && dialogE.isShowing()) return
            dialogE.show()
        }


    }

}