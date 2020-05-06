package com.crypto.eltwallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.ImportWalletModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ImportWalletActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout : RelativeLayout
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth_info: String

    lateinit var tabLayout: TabLayout
    lateinit var private_section: RelativeLayout
    lateinit var phone_section: RelativeLayout

    lateinit var walletname: EditText
    lateinit var walletaddress: EditText
    lateinit var walletpvtkey: EditText

    lateinit var input_walletname: String
    lateinit var input_walletaddress: String
    lateinit var input_walletpvtkey: String

    lateinit var btn_private_import: TextView
    lateinit var btn_phone_import: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_wallet)

        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        input_auth_info = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN,"").toString()

        linearLayout = findViewById(R.id.linearLayout)
        tabLayout = findViewById(R.id.tabLayout)
        private_section = findViewById(R.id.private_section)
        phone_section = findViewById(R.id.phone_section)

        walletname = findViewById(R.id.walletname)
        walletaddress = findViewById(R.id.walletaddress)
        walletpvtkey = findViewById(R.id.walletpvtkey)

        btn_private_import = findViewById(R.id.btn_private_import)
        btn_phone_import = findViewById(R.id.btn_phone_import)


        if(private_section.visibility == View.VISIBLE)
        {
            btn_private_import.setOnClickListener(){
                input_walletname = walletname.text.toString()
                input_walletaddress = walletaddress.text.toString()
                input_walletpvtkey = walletpvtkey.text.toString()

                if (Validations.validateForImportWallet(
                        input_walletname, input_walletaddress, input_walletpvtkey, linearLayout
                    )
                ) {
                    attemptToImportWallet()
                }

            }
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == 0) {
                    private_section.visibility = View.VISIBLE
                    phone_section.visibility = View.GONE

                } else if (tab.position == 1) {
                    private_section.visibility = View.GONE
                    phone_section.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }


    fun attemptToImportWallet() {
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.WALLET_NAME, input_walletname)
        weakHashMap.put(ConstantsRequest.WALLET_ADDRESS, input_walletaddress)
        weakHashMap.put(ConstantsRequest.WALLET_PVT_KEY, input_walletpvtkey)

        (RetrofitClient.getClient.attemptToImportWallet(input_auth_info, weakHashMap).enqueue(object :
            Callback<ImportWalletModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<ImportWalletModel>, response: Response<ImportWalletModel>) {

                Log.e("WalletError", response.body().toString())

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@ImportWalletActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@ImportWalletActivity, (response.body() as ImportWalletModel).getMessage().toString())
                }
                else if(response.code() == 200)
                {

//                        Log.e(
//                            TAG,
//                            "onResponseert dialog: "  + response.body()!!.getResponse_data()?.address)

//                    DialogBox.showError(this@ImportWalletActivity, (response.body() as ImportWalletModel).getMessage().toString())

                    val intent = Intent(this@ImportWalletActivity, BaseDashboardActivity::class.java)
                    intent.putExtra("type", "wallet")
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<ImportWalletModel>, th: Throwable) {

                Snackbar.make(DialogBoxInput.linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }
}
