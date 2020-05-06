package com.crypto.eltwallet.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.GasValueModel
import com.crypto.eltwallet.model.SendEltOtpModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.crypto.eltwallet.model.EltCurrentValueModel
import com.crypto.eltwallet.model.WalletTransactionLimitModel
import java.util.regex.Pattern


class SendActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: RelativeLayout
    lateinit var spinner: Spinner
    lateinit var gasAdapter: ArrayAdapter<*>
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth: String
    internal var list: MutableList<String> = ArrayList<String>()
    lateinit var back : ImageView

    lateinit var input_id: String
    lateinit var input_walletName: String
    lateinit var input_walletAddress: String
    lateinit var input_walletBal: String
    lateinit var input_dollarPrice: String
    lateinit var input_dollar: String
    lateinit var input_usedTransactionLimit: String
    lateinit var input_remainingTransactionLimit: String
    lateinit var input_transactionLimit: String
    var input_time_limit: String=""

    lateinit var walletName : TextView
    lateinit var walletAddress : TextView
    lateinit var eltBalance : TextView
    lateinit var dollarPrice : TextView

    lateinit var usedTransactionLimit : TextView
    lateinit var remainingTransactionLimit : TextView
    lateinit var transactionLimit : TextView

    lateinit var progressBar : ProgressBar

    lateinit var from : EditText
    lateinit var to : EditText
    lateinit var amount : EditText
    lateinit var usd_amount : EditText

    lateinit var btn_send: TextView

    lateinit var input_to: String
    lateinit var input_from : String
    lateinit var input_amount : String
    lateinit var input_gass_value : String
    lateinit var terms : CheckBox
    lateinit var camera : ImageView
    lateinit var max : TextView
    lateinit var max2 : TextView

    private val REQUEST_CODE_QR_SCAN = 101
    private val REQUEST_TO_SEND_ETH = 102
    private val CAMERA_PERMISSION = 103
    private val FINISH_PERMISSION = 420

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        supportActionBar?.hide()

        if (ContextCompat.checkSelfPermission(this@SendActivity, Manifest.permission.CAMERA) === PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@SendActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
        }

        terms = findViewById(R.id.terms)
        camera = findViewById(R.id.camera)
        camera.setOnClickListener() {

            val i = Intent(this@SendActivity, ScannerActivity::class.java)
            startActivityForResult(i, REQUEST_CODE_QR_SCAN)

        }

        max = findViewById(R.id.max)

        walletName = findViewById(R.id.walletName)
        walletAddress = findViewById(R.id.walletAddress)
        eltBalance = findViewById(R.id.eltBalance)
        dollarPrice = findViewById(R.id.dollarPrice)

        usedTransactionLimit = findViewById(R.id.usedTransactionLimit)
        remainingTransactionLimit = findViewById(R.id.remainingTransactionLimit)
        transactionLimit = findViewById(R.id.transactionLimit)

        from = findViewById(R.id.from)
        to = findViewById(R.id.to)
        amount = findViewById(R.id.amount)
        amount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 6))

        usd_amount = findViewById(R.id.usd_amount)

        progressBar = findViewById(R.id.progressBar)

        val intent = intent
        if (intent != null) {
//            input_id = intent.getStringExtra(ConstantsRequest.WALLET_ID)
//            input_walletName = intent.getStringExtra(ConstantsRequest.WALLET_NAME)
            input_walletAddress = intent.getStringExtra(ConstantsRequest.WALLET_ADDRESS)
//            input_walletBal = intent.getStringExtra(ConstantsRequest.WALLET_BALANCE)

//            input_dollarPrice = intent.getStringExtra(ConstantsRequest.DOLLAR_PRICE)
//            input_dollar = intent.getStringExtra("dollar")

//            input_usedTransactionLimit = intent.getStringExtra("usedTransactionLimit")
//            input_remainingTransactionLimit = intent.getStringExtra("remainingTransactionLimit")
//            input_transactionLimit = intent.getStringExtra("transactionLimit")
//            input_time_limit = intent.getStringExtra("time_limit")


//            walletName.setText(input_walletName)
            walletAddress.setText(input_walletAddress)
//            eltBalance.setText(input_walletBal + " ELT")
//            dollarPrice.setText("($" + input_dollarPrice + ")")

//            usedTransactionLimit.setText(Conversions.eltDecimals(input_usedTransactionLimit.toDouble()) + " ELT")
//            remainingTransactionLimit.setText(Conversions.eltDecimals(input_remainingTransactionLimit.toDouble()) + " ELT")
//            transactionLimit.setText(input_time_limit + " " + Conversions.eltDecimals(input_transactionLimit.toDouble()) + " ELT")
//
//            progressBar.visibility = View.VISIBLE
//            progressBar.max = (input_transactionLimit.toDouble()*1000000).toInt()
//            progressBar.progress = (input_remainingTransactionLimit.toDouble()*1000000).toInt()

            from.setText(input_walletAddress)

            if(intent.getStringExtra("to")!=null) {
                input_to = intent.getStringExtra(ConstantsRequest.TO)
                to.setText(input_to)

                input_amount = intent.getStringExtra(ConstantsRequest.AMOUNT)
                amount.setText(input_amount)
            }

        }

        max.setOnClickListener {

            amount.setText(Conversions.eltDecimals(input_walletBal.toDouble()))

        }

        amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (charSequence.length == 0) {
                    usd_amount.setText("")
                    return
                }

                val changedElt = java.lang.Float.valueOf(charSequence.toString())
                val result = changedElt * input_dollar.toDouble()

                usd_amount.setText(String.format("%.2f", result))

            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        linearLayout = findViewById(R.id.linearLayout)
        spinner = findViewById(R.id.spinner)
        back = findViewById(R.id.back)
        back.setOnClickListener{
            finish()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {

                val temp_input_gass_value : String = spinner.selectedItem.toString()

                input_gass_value = temp_input_gass_value.substring(temp_input_gass_value.indexOf("(")+1, temp_input_gass_value.indexOf(")"))

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        sharedPreferenceUtil = SharedPreferenceUtil(this@SendActivity)
        input_auth =
            sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        gasAdapter = ArrayAdapter(this@SendActivity, R.layout.spinner, list)
        spinner.setAdapter(gasAdapter)


        attemptToGetWalletTransactionLimit()

        attemptToGasValue()

        btn_send= findViewById(R.id.btn_send)
        btn_send.setOnClickListener{

            input_to = to.text.toString()
            input_from = from.text.toString()
            input_amount = amount.text.toString()

            if (Validations.validateForSend(
                    input_to, input_from, input_amount, linearLayout
                )
            ) {
                if(terms.isChecked){

                    attemptToSendEltOtp()
                }
                else {
                    Toast.makeText(this@SendActivity, R.string.please_accept, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                to.setText(result)
                if (result != null) {
                    to.setSelection(result.length)
                }
            }
        }
        else if (requestCode == FINISH_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                finish()
            }
        }
    }

    fun attemptToEltCurrentValue() {

        (RetrofitClient.getClient.attemptToEltCurrentValue(input_auth).enqueue(object :
            Callback<EltCurrentValueModel> {
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<EltCurrentValueModel>,
                response: Response<EltCurrentValueModel>
            ) {

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@SendActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@SendActivity, (response.body() as EltCurrentValueModel).message)
                }
                else if(response.code() == 200)
                {
                    input_dollar = (response.body() as EltCurrentValueModel).current_value.toString()

                    if(intent.getStringExtra("to")!=null) {

                        usd_amount.setText((input_amount.toDouble() * input_dollar.toDouble()).toString())
                        dollarPrice.setText("($" + Conversions.dollarDecimals(input_walletBal.toDouble() * input_dollar.toDouble()) + ")")

                    }

                }

            }

            override fun onFailure(call: Call<EltCurrentValueModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

    fun attemptToGetWalletTransactionLimit() {

        (RetrofitClient.getClient.attemptToGetWalletTransactionLimit(
            input_auth,
            input_walletAddress
        ).enqueue(object :
            Callback<WalletTransactionLimitModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<WalletTransactionLimitModel>,
                response: Response<WalletTransactionLimitModel>
            ) {

                Log.e("Response", response.body().toString())

                if (response == null) {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    DialogBox.showError(this@SendActivity, (response.errorBody()!!.string()).toString())
                } else if (response.code() == 202) {
                    DialogBox.showError(this@SendActivity, (response.body() as WalletTransactionLimitModel).message)
                } else if (response.code() == 200) {

                    attemptToEltCurrentValue()

                    input_walletName = ((response.body() as WalletTransactionLimitModel).wallet_name)

                    var w : String = ((response.body() as WalletTransactionLimitModel).wallet_balance)
                    var elt = w.substring(0, w.length - 4)
                    input_walletBal = Conversions.eltDecimals(elt.toDouble())

//                    input_walletBal = ((response.body() as WalletTransactionLimitModel).wallet_balance)

                    walletName.setText(input_walletName)
                    eltBalance.setText(input_walletBal + " ELT")


                    var limit: String =
                        (response.body() as WalletTransactionLimitModel).countAsTransactionLimit

                    if (limit.equals("Day")) {
                        input_time_limit = resources.getString(R.string.daily_limits)
                    } else if (limit.equals("Week")) {
                        input_time_limit = resources.getString(R.string.weekly_limits)
                    } else if (limit.equals("Month")) {
                        input_time_limit = resources.getString(R.string.monthly_limits)
                    }

                    input_usedTransactionLimit = ((response.body() as WalletTransactionLimitModel).usedTransactionLimit).toString()
                    input_remainingTransactionLimit = ((response.body() as WalletTransactionLimitModel).remainingTransactionLimit).toString()
                    input_transactionLimit = ((response.body() as WalletTransactionLimitModel).transactionLimit).toString()

                    usedTransactionLimit.setText(Conversions.eltDecimals(input_usedTransactionLimit.toDouble()) + " ELT")
                    remainingTransactionLimit.setText(Conversions.eltDecimals(input_remainingTransactionLimit.toDouble()) + " ELT")
                    transactionLimit.setText(input_time_limit + " " + Conversions.eltDecimals(input_transactionLimit.toDouble()) + " ELT")

                    progressBar.visibility = View.VISIBLE
                    progressBar.max = (input_transactionLimit.toDouble()*1000000).toInt()
                    progressBar.progress = (input_remainingTransactionLimit.toDouble()*1000000).toInt()

                }

            }

            override fun onFailure(call: Call<WalletTransactionLimitModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))

    }

    fun attemptToGasValue() {
        (RetrofitClient.getClient.attemptToGasValue(input_auth).enqueue(object :
            Callback<GasValueModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<GasValueModel>,
                response: Response<GasValueModel>
            ) {

                list.clear()

                for (i in 0 until response.body()!!.getGasValue()!!.size) {

                    var gasValueModel = GasValueModel.DataBean()

                    gasValueModel.gid = response.body()!!.getGasValue()?.get(i)?.gid
                    gasValueModel.name = response.body()!!.getGasValue()?.get(i)?.name
                    gasValueModel.gass_value = response.body()!!.getGasValue()?.get(i)?.gass_value
                    gasValueModel.timestamp = response.body()!!.getGasValue()?.get(i)?.timestamp

                    list.add(response.body()!!.getGasValue()?.get(i)?.name.toString() + " (" + response.body()!!.getGasValue()?.get(i)?.gass_value.toString() + ")")

                }

                gasAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<GasValueModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }

    fun attemptToSendEltOtp() {
        DialogBox.showLoader(this@SendActivity)
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.FROM, input_walletAddress)
        weakHashMap.put(ConstantsRequest.TO, input_to)
        weakHashMap.put(ConstantsRequest.AMOUNT, input_amount)
        weakHashMap.put(ConstantsRequest.GASS_VALUE, input_gass_value)

        (RetrofitClient.getClient.attemptToSendEltOtp(input_auth, weakHashMap).enqueue(object :
            Callback<SendEltOtpModel> {
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<SendEltOtpModel>,
                response: Response<SendEltOtpModel>
            ) {
                DialogBox.closeDialogE()
                if (response == null) {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    DialogBox.showError(this@SendActivity, (response.errorBody()!!.string()).toString())
                } else if (response.code() == 202) {
                    DialogBox.showError(this@SendActivity, (response.body() as SendEltOtpModel).message)
                } else if (response.code() == 200) {
                    DialogBox.showSendEltOtp(this@SendActivity, input_auth, linearLayout)
                }
            }

            override fun onFailure(call: Call<SendEltOtpModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }

}

internal class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) : InputFilter {

    var mPattern: Pattern

    init {
        mPattern =
            Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val matcher = mPattern.matcher(dest)
        return if (!matcher.matches()) "" else null
    }
}