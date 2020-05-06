package com.crypto.eltwallet.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.adapter.WalletTransactionAdapter
import com.crypto.eltwallet.model.WalletTransactionLimitModel
import com.crypto.eltwallet.model.WalletTransactionModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.pagination.PaginationListener
import com.crypto.eltwallet.pagination.PaginationListener.Companion.PAGE_SIZE
import com.crypto.eltwallet.pagination.PaginationListener.Companion.PAGE_START
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WalletDetailsActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName

    lateinit var input_id: String
    lateinit var input_walletName: String
    lateinit var input_walletAddress: String
    lateinit var input_walletBal: String
    lateinit var input_dollarPrice: String
    lateinit var input_dollar: String

    lateinit var walletName : TextView
    lateinit var walletAddress : TextView
    lateinit var eltBalance : TextView
    lateinit var dollarPrice : TextView

    lateinit var usedTransactionLimit : TextView
    lateinit var remainingTransactionLimit : TextView
    lateinit var transactionLimit : TextView

    lateinit var back : ImageView
    lateinit var input_auth: String
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var linearLayout : LinearLayout

    lateinit var more : ImageView
    lateinit var progressBar : ProgressBar

    lateinit var recycler_view : RecyclerView
    private val arrayList = ArrayList<WalletTransactionModel.DataBean>()
    private val arrayList_sent = ArrayList<WalletTransactionModel.DataBean>()
    private val arrayList_receive = ArrayList<WalletTransactionModel.DataBean>()

    lateinit var tabLayout: TabLayout
    lateinit var walletTransactionAdapter: WalletTransactionAdapter
    lateinit var receive : LinearLayout
    lateinit var send : LinearLayout
    lateinit var tv_send : TextView

    lateinit var input_usedTransactionLimit : String
    lateinit var input_remainingTransactionLimit : String
    lateinit var input_transactionLimit : String
    var flag_send = false
    var flag_send2 = false
    var flag_list = false

    var time_limit: String = ""
    lateinit var no_transactions : TextView
    lateinit var todate : ImageView
    lateinit var clear : TextView
    lateinit var tv_date : TextView
    var input_todate : String = ""
    //pagination
    var page_number : Int = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var isPagination = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_details)

        supportActionBar?.hide()

        WalletTransactionAdapter.type = "all"

        sharedPreferenceUtil = SharedPreferenceUtil(applicationContext)
        input_auth = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        recycler_view = findViewById(R.id.recycler_view)
        linearLayout = findViewById(R.id.linearLayout)


        back = findViewById(R.id.back)
        back.setOnClickListener{
            finish()
        }

        no_transactions= findViewById(R.id.no_transactions)

        walletName = findViewById(R.id.walletName)
        walletAddress = findViewById(R.id.walletAddress)
        eltBalance = findViewById(R.id.eltBalance)
        dollarPrice = findViewById(R.id.dollarPrice)

        usedTransactionLimit = findViewById(R.id.usedTransactionLimit)
        remainingTransactionLimit = findViewById(R.id.remainingTransactionLimit)
        transactionLimit = findViewById(R.id.transactionLimit)

        val intent = intent
        if (intent != null) {
            input_id = intent.getStringExtra(ConstantsRequest.WALLET_ID)
            input_walletName = intent.getStringExtra(ConstantsRequest.WALLET_NAME)
            input_walletAddress = intent.getStringExtra(ConstantsRequest.WALLET_ADDRESS)
            input_walletBal = intent.getStringExtra(ConstantsRequest.WALLET_BALANCE)
            input_dollarPrice = intent.getStringExtra(ConstantsRequest.DOLLAR_PRICE)
            input_dollar = intent.getStringExtra("dollar")

            walletName.setText(input_walletName)
            walletAddress.setText(input_walletAddress)
            eltBalance.setText(input_walletBal + " ELT")
            dollarPrice.setText("($" + input_dollarPrice + ")")

        }

        receive = findViewById(R.id.receive)
        receive.setOnClickListener {
            DialogBox.showReceive(this@WalletDetailsActivity, input_walletAddress)
        }

        tv_send = findViewById(R.id.tv_send)
        send = findViewById(R.id.send)
        send.setOnClickListener {

            if(flag_send) {

                    val intent = Intent(this@WalletDetailsActivity, SendActivity::class.java)
                    intent.putExtra(ConstantsRequest.WALLET_ID, input_id)
                    intent.putExtra(ConstantsRequest.WALLET_NAME, CapitalUtils.capitalize(input_walletName))
                    intent.putExtra(ConstantsRequest.WALLET_ADDRESS, input_walletAddress)
                    intent.putExtra(ConstantsRequest.WALLET_BALANCE, input_walletBal)
                    intent.putExtra(ConstantsRequest.DOLLAR_PRICE, input_dollarPrice)
                    intent.putExtra("dollar", input_dollar)
                    intent.putExtra("usedTransactionLimit", input_usedTransactionLimit)
                    intent.putExtra("remainingTransactionLimit", input_remainingTransactionLimit)
                    intent.putExtra("transactionLimit", input_transactionLimit)
                    intent.putExtra("time_limit", time_limit)
                    startActivity(intent)


            }
            else {

                if(flag_send2) {

                    DialogBox.showMessage(this@WalletDetailsActivity, resources.getString(R.string.there_is_no_elt))

                }

            }

        }

        tabLayout = findViewById(R.id.tabLayout)

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == 0) {

                    if(flag_list){

                        WalletTransactionAdapter.type = "all"
                        walletTransactionAdapter.notifyDataSetChanged()

                    }


                } else if (tab.position == 1) {

                    if(flag_list){

                        if(arrayList_sent.size == 0) {

                            no_transactions.visibility = View.VISIBLE

                        }
                        else {

                            no_transactions.visibility = View.GONE

                        }

                        WalletTransactionAdapter.type = "sent"
                        walletTransactionAdapter.notifyDataSetChanged()

                    }

                } else if (tab.position == 2) {

                    if(flag_list){

                        if(arrayList_receive.size == 0) {

                            no_transactions.visibility = View.VISIBLE

                        }
                        else {

                            no_transactions.visibility = View.GONE

                        }

                        WalletTransactionAdapter.type = "received"
                        WalletTransactionAdapter.isLoaderVisible=false
                        walletTransactionAdapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        progressBar = findViewById(R.id.progressBar)

        more = findViewById(R.id.more)
        more.setOnClickListener {

            val popupMenu: PopupMenu = PopupMenu(this,more)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {

                    R.id.action_rename ->
                        DialogBoxInput.showRenameWallet(this@WalletDetailsActivity, input_id, input_auth, walletName.text.toString(), walletName)

                    R.id.action_delete ->
                        DialogBox.showWalletDelete(this@WalletDetailsActivity, input_id, input_auth, linearLayout)
                }
                true
            })
            popupMenu.show()

        }

        tv_date = findViewById(R.id.tv_date)

        clear = findViewById(R.id.clear)
        clear.setOnClickListener{

            clear.visibility = View.INVISIBLE
            todate.visibility = View.VISIBLE
            tv_date.setText("")
            input_todate = ""

            DialogBox.showLoader(this)
            walletTransactionAdapter.clear()
            page_number = PaginationListener.PAGE_START
            isLastPage = false
            isPagination =false
            attemptTransactionList()

        }

        todate = findViewById(R.id.todate)
        todate.setOnClickListener{

//            val listener =
//                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//                    dob.setText(
//                        String.format("%02d", dayOfMonth) + "-" + String.format(
//                            "%02d",
//                            monthOfYear + 1
//                        ) + "-" + year
//                    )
//                    //dob.setSelection(dob.getText().length());
//                }//R.style.DatePickerDialogTheme
//            val dpDialog =
//                DatePickerDialog(context, R.style.DatePickerDialogTheme, listener, year, month, day)
//            dpDialog.datePicker.maxDate = System.currentTimeMillis()

            val dateFormatter = SimpleDateFormat("MM-dd-yyyy", Locale.US)
            val newCalendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this@WalletDetailsActivity, R.style.DatePickerDialogTheme,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, monthOfYear, dayOfMonth)

                    clear.visibility = View.VISIBLE
                    todate.visibility = View.GONE
                    input_todate = dateFormatter.format(newDate.time);
                    tv_date.setText(input_todate)

                    DialogBox.showLoader(this)
                    walletTransactionAdapter.clear()
                    page_number = PaginationListener.PAGE_START
                    isLastPage = false
                    isPagination =false
                    attemptTransactionList()

                },
                newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())
            datePickerDialog.show()

            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorPrimary))
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)

        }

        //pagination
        recycler_view.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recycler_view.setLayoutManager(layoutManager)

        walletTransactionAdapter = WalletTransactionAdapter(ArrayList(), ArrayList(), ArrayList(),
            this@WalletDetailsActivity, input_dollar)
        recycler_view.adapter = walletTransactionAdapter

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems()
            {
                isPagination = true
                isLoading = true
                page_number++
                attemptTransactionList()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        //
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            attemptToGetWalletTransactionLimit()
        }
        else
        {
            Snackbar.make(linearLayout, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }

    }

    override fun onResume() {
        super.onResume()
       // walletTransactionAdapter.clear()
        if (TransactionDetailsActivity.isBackFromTransdetails)
        {
            TransactionDetailsActivity.isBackFromTransdetails = false
        }
        else
        {
            DialogBox.showLoader(this)
            walletTransactionAdapter.clear()
            page_number = PaginationListener.PAGE_START
            isLastPage = false
            isPagination =false
            attemptTransactionList()
        }

    }

    fun attemptToGetWalletTransactionLimit()
    {
        (RetrofitClient.getClient.attemptToGetWalletTransactionLimit(input_auth,
            input_walletAddress).enqueue(object :
            Callback<WalletTransactionLimitModel>
        {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<WalletTransactionLimitModel>, response: Response<WalletTransactionLimitModel>)
            {
                Log.e("Response", response.body().toString())

                if (response == null) {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    DialogBox.showError(this@WalletDetailsActivity, (response.errorBody()!!.string()).toString())
                } else if (response.code() == 202) {
                    DialogBox.showError(this@WalletDetailsActivity, (response.body() as WalletTransactionLimitModel).message)
                } else if (response.code() == 200) {


                    if(input_walletBal.toFloat() == 0F ) {

                        flag_send = false
                        flag_send2 = true

                    }
                    else {

                        flag_send = true
                    }

                    var limit: String =
                        (response.body() as WalletTransactionLimitModel).countAsTransactionLimit

                    if (limit.equals("Day")) {
                        time_limit = resources.getString(R.string.daily_limits)
                    } else if (limit.equals("Week")) {
                        time_limit = resources.getString(R.string.weekly_limits)
                    } else if (limit.equals("Month")) {
                        time_limit = resources.getString(R.string.monthly_limits)
                    }

                    input_usedTransactionLimit =
                        ((response.body() as WalletTransactionLimitModel).usedTransactionLimit).toString()
                    input_remainingTransactionLimit =
                        ((response.body() as WalletTransactionLimitModel).remainingTransactionLimit).toString()
                    input_transactionLimit =
                        ((response.body() as WalletTransactionLimitModel).transactionLimit).toString()

                    usedTransactionLimit.setText(Conversions.eltDecimals(input_usedTransactionLimit.toDouble()) + " ELT")
                    remainingTransactionLimit.setText(
                        Conversions.eltDecimals(
                            input_remainingTransactionLimit.toDouble()
                        ) + " ELT"
                    )
                    transactionLimit.setText(
                        time_limit + " " + Conversions.eltDecimals(
                            input_transactionLimit.toDouble()
                        ) + " ELT"
                    )

                    progressBar.visibility = View.VISIBLE
                    progressBar.max = (input_transactionLimit.toDouble()*1000000).toInt()
                    progressBar.progress = (input_remainingTransactionLimit.toDouble()*1000000).toInt()

                }

            }

            override fun onFailure(call: Call<WalletTransactionLimitModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))

    }


    fun attemptTransactionList()
    {
//        skeletonScreen =
//            Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
//                .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

        (RetrofitClient.getClient.attemptTransactionList(input_auth, input_walletAddress, PAGE_SIZE, page_number,
            "desc", input_todate).enqueue(object :
            Callback<WalletTransactionModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<WalletTransactionModel>, response: Response<WalletTransactionModel>)
            {
                DialogBox.closeDialogE()

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401 || response.body()!!.getStatusCode() == 401)
                {
                    DialogBox.showError(this@WalletDetailsActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code()==200)
                {

//                Log.e("jbjbjbj", input_walletAddress + "/" + input_todate )

                    if (!isPagination)
                    {
                        arrayList.clear()
                        arrayList_sent.clear()
                        arrayList_receive.clear()
                    }

                    var size : Int = response.body()!!.getTransactionList()!!.size

                    if(size == 0)
                    {

                        /*no_transactions.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE*/


                        if (page_number == 1)
                        {
                            no_transactions.visibility = View.VISIBLE
                            recycler_view.visibility = View.GONE
                        }
                        else
                        {
                            walletTransactionAdapter.emptyData()
                            page_number--
                        }

                        isLastPage = true

                    }
                    else
                    {

                        val allList = ArrayList<WalletTransactionModel.DataBean>()
                        val sentList = ArrayList<WalletTransactionModel.DataBean>()
                        val receiveList = ArrayList<WalletTransactionModel.DataBean>()

                        flag_list = true

                        no_transactions.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE

                        for (i in 0 until size) {

                            Log.e(TAG,"onResponseertdata: " + i + "   " + response.body()!!.getTransactionList()?.get(i))
                            Log.e(TAG,"onResponseertdata: " + i + "   " + response.body()!!.getTransactionList()?.get(i)?.gasPriceBean())
                            Log.e(TAG,"onResponseertdata: " + i + "   " + response.body()!!.getTransactionList()?.get(i)?.gasPriceBean()?.c)

                            val walletTransactionModel = WalletTransactionModel.DataBean()

                            walletTransactionModel._id = response.body()!!.getTransactionList()?.get(i)?._id
                            walletTransactionModel.blockHash = response.body()!!.getTransactionList()?.get(i)?.blockHash
                            walletTransactionModel.blockNumber = response.body()!!.getTransactionList()?.get(i)?.blockNumber!!
                            walletTransactionModel.from = response.body()!!.getTransactionList()?.get(i)?.from
                            walletTransactionModel.gas = response.body()!!.getTransactionList()?.get(i)?.gas
                            walletTransactionModel.gasPrice = response.body()!!.getTransactionList()?.get(i)?.gasPrice
                            walletTransactionModel.hash = response.body()!!.getTransactionList()?.get(i)?.hash
                            walletTransactionModel.input = response.body()!!.getTransactionList()?.get(i)?.input
                            walletTransactionModel.nonce = response.body()!!.getTransactionList()?.get(i)?.nonce!!
                            walletTransactionModel.to = response.body()!!.getTransactionList()?.get(i)?.to
                            walletTransactionModel.transactionIndex = response.body()!!.getTransactionList()?.get(i)?.transactionIndex!!
                            walletTransactionModel.value = response.body()!!.getTransactionList()?.get(i)?.value
                            walletTransactionModel.v = response.body()!!.getTransactionList()?.get(i)?.v
                            walletTransactionModel.r = response.body()!!.getTransactionList()?.get(i)?.r
                            walletTransactionModel.s = response.body()!!.getTransactionList()?.get(i)?.s
                            walletTransactionModel.timestamp = response.body()!!.getTransactionList()?.get(i)?.timestamp!!
                            walletTransactionModel.is_sender = response.body()!!.getTransactionList()?.get(i)?.is_sender
                            walletTransactionModel.gasPriceBean()?.c = response.body()!!.getTransactionList()?.get(i)?.gasPriceBean()?.c

                            walletTransactionModel.blockHash = response.body()!!.getTransactionList()?.get(i)?.blockHash

                            arrayList.add(walletTransactionModel)
                            allList.add(walletTransactionModel)

                            if(response.body()!!.getTransactionList()?.get(i)?.is_sender.equals("true")) {
                                arrayList_sent.add(walletTransactionModel)
                                sentList.add(walletTransactionModel)
                            }
                            else {
                                arrayList_receive.add(walletTransactionModel)
                                receiveList.add(walletTransactionModel)
                            }

                        }

                        /*walletTransactionAdapter = WalletTransactionAdapter(arrayList, arrayList_sent, arrayList_receive, this@WalletDetailsActivity, input_dollar)
                        recycler_view.adapter = walletTransactionAdapter*/

                        if (page_number !== PAGE_START) walletTransactionAdapter.removeLoading()
                        walletTransactionAdapter.addItems(allList,sentList,receiveList)

                        if(!isLastPage)
                        {
                            walletTransactionAdapter.addLoading()
                        }

                    }
                    isLoading = false
                }


            }

            override fun onFailure(call: Call<WalletTransactionModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }

}