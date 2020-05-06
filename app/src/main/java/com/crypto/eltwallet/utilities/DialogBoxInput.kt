package com.crypto.eltwallet.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.CreateWalletSuccessActivity
import com.crypto.eltwallet.model.CreateWalletModel
import com.crypto.eltwallet.model.RenameWalletModel
import com.crypto.eltwallet.network.RetrofitClient
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.view.inputmethod.InputMethodManager



class DialogBoxInput {

    companion object {

        private val TAG = this::class.java.simpleName
        lateinit var dialogE: Dialog
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
        lateinit var linearLayout : RelativeLayout

        fun showCreateWallet(context: Context) {

            this.context = context

//            showKeyboard()

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_create_wallet)
            dialogE.setCancelable(false)
                dialogE.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            linearLayout = dialogE.findViewById(R.id.linearLayout)

            var sharedPreferenceUtil : SharedPreferenceUtil = SharedPreferenceUtil(context)
            var input_auth_info = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN,"").toString()

            var walletname = dialogE.findViewById(R.id.walletname) as EditText
            var input_wallet_name : String

            val dialogButtonCancel = dialogE.findViewById(R.id.dialogButtonCancel) as Button
            dialogButtonCancel.setOnClickListener {
                dialogE.dismiss()
            }

            val dialogButtonCreate = dialogE.findViewById(R.id.dialogButtonCreate) as Button
            dialogButtonCreate.setOnClickListener {
                dialogE.dismiss()
                input_wallet_name = walletname.text.toString()
                attemptToCreateWallet(context,input_auth_info, input_wallet_name)

            }

            if (dialogE != null && dialogE.isShowing())
                return

            val window = dialogE.getWindow()

            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            dialogE.show()
        }


        fun attemptToCreateWallet(context: Context,input_auth_info: String, input_wallet_name: String) {
            DialogBox.showLoader(context)
            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put("walletname", input_wallet_name)

            (RetrofitClient.getClient.attemptToCreateWallet(input_auth_info, weakHashMap).enqueue(object :
                Callback<CreateWalletModel> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<CreateWalletModel>, response: Response<CreateWalletModel>) {
                    DialogBox.closeDialogE()
                    Log.e("WalletError", response.body().toString())

                    if(response == null)
                    {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        dialogE.dismiss()
                        DialogBox.showError(context, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(context, (response.body() as CreateWalletModel).getMessage().toString())
                    }
                    else if(response.code() == 200)
                    {
                        val intent = Intent(context, CreateWalletSuccessActivity::class.java)
                        intent.putExtra("address", response.body()!!.getResponse_data()?.address)
                        intent.putExtra("privateKey", response.body()!!.getResponse_data()?.privateKey)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }

                }

                override fun onFailure(call: Call<CreateWalletModel>, th: Throwable) {
                    DialogBox.closeDialogE()
                    Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

                }
            }))
        }


        fun showRenameWallet(context: Context, input_id : String, input_auth_info: String, input_walletName : String, walletName : TextView) {

            this.context = context

            dialogE = Dialog(context)
            dialogE.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogE.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogE.setContentView(R.layout.dialog_rename_wallet)
            dialogE.setCancelable(false)

            linearLayout = dialogE.findViewById(R.id.linearLayout)

            var sharedPreferenceUtil : SharedPreferenceUtil = SharedPreferenceUtil(context)
            var input_auth_info = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN,"").toString()

            var walletname = dialogE.findViewById(R.id.walletname) as EditText
            walletname.setText(input_walletName)
            walletname.setSelection(input_walletName.length)
            var input_wallet_name : String

            val dialogButtonCancel = dialogE.findViewById(R.id.dialogButtonCancel) as Button
            dialogButtonCancel.setOnClickListener {
                dialogE.dismiss()
            }

            val dialogButtonCreate = dialogE.findViewById(R.id.dialogButtonCreate) as Button
            dialogButtonCreate.setOnClickListener {
                input_wallet_name = walletname.text.toString()

                attemptToRenameWallet(input_id, input_auth_info, input_wallet_name, walletName)

            }

            if (dialogE != null && dialogE.isShowing())
                return
            dialogE.show()
        }


        fun attemptToRenameWallet(input_id : String, input_auth_info: String, input_wallet_name: String, walletName: TextView) {
            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.WALLET_ID, input_id)
            weakHashMap.put(ConstantsRequest.WALLET_NAME, input_wallet_name)

            (RetrofitClient.getClient.attemptToRenameWallet(input_auth_info, weakHashMap).enqueue(object :
                Callback<RenameWalletModel> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<RenameWalletModel>, response: Response<RenameWalletModel>) {

                    Log.e("WalletError", response.body().toString())

                    if(response == null)
                    {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        DialogBox.showError(context, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(context, (response.body() as RenameWalletModel).message)
                    }
                    else if(response.code() == 200)
                    {
                        walletName.setText(input_wallet_name)

                        dialogE.dismiss()
                        Toast.makeText(context, R.string.rename_successfully, Toast.LENGTH_SHORT).show()

                    }

                }

                override fun onFailure(call: Call<RenameWalletModel>, th: Throwable) {

                    Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

                }
            }))
        }


        fun showKeyboard() {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun closeKeyboard() {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

    }

}