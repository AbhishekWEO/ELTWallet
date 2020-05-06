package com.crypto.eltwallet.utilities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.RelativeLayout
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.BaseDashboardActivity

class DialogBoxWalletSuccess {

    companion object {

        private val TAG = this::class.java.simpleName
        lateinit var dialogE: Dialog
        lateinit var context: Context
        lateinit var linearLayout: RelativeLayout

        fun showWarning(context: Context) {

            this.context = context

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_wallet_success)
            dialogE.setCancelable(false)

            linearLayout = dialogE.findViewById(R.id.linearLayout)

            var sharedPreferenceUtil: SharedPreferenceUtil = SharedPreferenceUtil(context)
            var input_auth_info =
                sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()


            val dialogButtonCancel = dialogE.findViewById(R.id.dialogButtonCancel) as Button
            dialogButtonCancel.setOnClickListener {
                dialogE.dismiss()
            }

            val dialogButtonYes = dialogE.findViewById(R.id.dialogButtonYes) as Button
            dialogButtonYes.setOnClickListener {

                val intent = Intent(context, BaseDashboardActivity::class.java)
                intent.putExtra("type", "wallet")
                context.startActivity(intent)
                (context as Activity).finish()

            }

            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }
    }
}