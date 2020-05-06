package com.crypto.eltwallet.fragments_dashborad


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide

import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.DashboardModel
import com.crypto.eltwallet.model.JsonListModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat


class HomeFragment : Fragment(), OnChartValueSelectedListener {


    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: RelativeLayout
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth_info: String

    lateinit var lineChart: LineChart
    lateinit var XAxisFormatter: ValueFormatter
    val dayCommaTimeDateFormatter = TimeDateFormatter()
    private var chartFillColor: Int = 0
    private var chartBorderColor: Int = 0
    private var percentageColor: Int = 0

    lateinit var image: ImageView
    lateinit var username: TextView
    lateinit var totalBalance: TextView
    lateinit var totalBalanceUSD: TextView
    lateinit var totalWallets: TextView
    lateinit var totalTransactions: TextView
    lateinit var sentTransactions: TextView
    lateinit var receivedTransactions: TextView
    lateinit var countAsTransactionLimit: TextView
    lateinit var userTransactionLimit: TextView
    lateinit var current_price: TextView
    lateinit var percentage_month: TextView

    lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val context = context as Activity

        lineChart = view.findViewById(R.id.chart)
        lineChart.setOnChartValueSelectedListener(this)
        lineChart.setNoDataText(getString(R.string.noChartDataString))
        lineChart.setNoDataTextColor(R.color.colorPrimary)

        linearLayout = view.findViewById(R.id.linearLayout)
        sharedPreferenceUtil = SharedPreferenceUtil(context)
        input_auth_info =
            sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        image = view.findViewById(R.id.image)
        username = view.findViewById(R.id.username)
        totalBalance = view.findViewById(R.id.totalBalance)
        totalBalanceUSD = view.findViewById(R.id.totalBalanceUSD)
        totalWallets = view.findViewById(R.id.totalWallets)
        totalTransactions = view.findViewById(R.id.totalTransactions)
        sentTransactions = view.findViewById(R.id.sentTransactions)
        receivedTransactions = view.findViewById(R.id.receivedTransactions)
        countAsTransactionLimit = view.findViewById(R.id.countAsTransactionLimit)
        userTransactionLimit = view.findViewById(R.id.userTransactionLimit)
        current_price = view.findViewById(R.id.current_price)
        percentage_month = view.findViewById(R.id.percentage_month)

        tabLayout = view.findViewById(R.id.tabLayout)

        attemptToDashboardData()
        attemptToJsonList(1, context)

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
//                viewPager!!.currentItem = tab.position

                if (tab.position == 0) {
                    attemptToJsonList(1, context)
                } else if (tab.position == 1) {
                    attemptToJsonList(3, context)
                } else if (tab.position == 2) {
                    attemptToJsonList(6, context)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        return view
    }

    override fun onNothingSelected() {

        Log.e("Nothing Selected", "Nothing Selected")
        /*TODO("not implemented") //To change body of created functions use File | Settings | File Templates.*/
    }

    override fun onValueSelected(e: Entry, h: Highlight) {

        val context = context as Activity

        val miliSec = e.x.toLong()

        // Creating date format
        val simple = SimpleDateFormat("MMM dd, yyyy")

        // Creating date from milliseconds
        // using Date() constructor
        val result = Date(miliSec)

        // Formatting Date according to the
        // given format
        val a = simple.format(result)

        // txtvalue.setText(String.format(getString(R.string.unrounded_usd_chart_price_format), a ));
        // txtvalue.setText(a + " ($" + e.y + ")")

        var mv: MyMarkerView =
            MyMarkerView(context, R.layout.custom_marker_view_layout, e.y, a.toString())
        mv.setChartView(lineChart)
        lineChart.setMarker(mv)

    }

    fun attemptToDashboardData() {
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
//        weakHashMap.put(ConstantsRequest.EMAIL, input_email)
//        if (this.preferenceUtil.getString(
//                SharedPreferenceConstant.language,
//                ""
//            ).equalsIgnoreCase("Chinese")
//        ) {
//            weakHashMap.put(Constants.lang, "zh")
//        } else {
//            weakHashMap.put(Constants.lang, "en")
//        }
//        this.progress = ProgressDialog(this.context)
//        this.progress.setMessage("Please wait")
//        this.progress.setProgressStyle(0)
//        this.progress.setIndeterminate(true)
//        this.progress.setCancelable(false)
//        this.progress.show()
//        Log.e("Sending data", weakHashMap.toString())

        (RetrofitClient.getClient.attemptToDashboardData(input_auth_info).enqueue(object :
            Callback<DashboardModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<DashboardModel>,
                response: Response<DashboardModel>
            ) {

                if (response == null) {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                } else if (response.code() == 401) {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                } else if (response.code() == 202) {
                    DialogBox.showError(activity!!, (response.body() as DashboardModel).message)
                } else if (response.code() == 200) {

                    try {
                        Glide.with(activity!!)
                            .load((response.body() as DashboardModel).user_image)
                            .into(image)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    username.setText(CapitalUtils.capitalize((response.body() as DashboardModel).firstname + " " + (response.body() as DashboardModel).lastname))

                    var elt_balance = (response.body() as DashboardModel).totalBalance
                    var balance: String = Conversions.eltDecimals(elt_balance)
                    totalBalance.setText(balance + " ELT")

                    var dollar_price: Double = (response.body() as DashboardModel).dollarPrice
                    var usd_price: String = Conversions.dollarDecimals(elt_balance * dollar_price)
                    totalBalanceUSD.setText("($" + usd_price + " USD)")

                    totalWallets.setText((response.body() as DashboardModel).totalWallets)
                    totalTransactions.setText(((response.body() as DashboardModel).totalTransactions).toString())
                    sentTransactions.setText(((response.body() as DashboardModel).sentTransactions).toString())
                    receivedTransactions.setText(((response.body() as DashboardModel).receivedTransactions).toString())

                    var limit: String = (response.body() as DashboardModel).countAsTransactionLimit

                    if (limit.equals("Day")) {
                        countAsTransactionLimit.setText(R.string.daily_limit)
                    } else if (limit.equals("Week")) {
                        countAsTransactionLimit.setText(R.string.weekly_limit)
                    } else if (limit.equals("Month")) {
                        countAsTransactionLimit.setText(R.string.monthly_limit)
                    }

                    var elt: String = String.format("%.0f", (response.body() as DashboardModel).userTransactionLimit)

                    userTransactionLimit.setText(elt + " ELT")

                    current_price.setText("1 ELT = $" + ((response.body() as DashboardModel).dollarPrice).toString())

                }

            }

            override fun onFailure(call: Call<DashboardModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }

    fun attemptToJsonList(graphTimeSpan: Int, context: Context) {
        DialogBox.showLoader(activity!!)//21-04-2020
        lineChart.isEnabled = true
        lineChart.clear()

        (RetrofitClient.getClient.attemptToJsonList(graphTimeSpan).enqueue(object :
            Callback<JsonListModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<JsonListModel>,
                response: Response<JsonListModel>
            ) {

                if (isAdded()) {

                    var dollars: MutableList<Float> = ArrayList()
                    var timestamp: MutableList<Float> = ArrayList()
                    var max_dollar: Float = 0.0F

                    for (i in 0 until response.body()!!.getData()!!.size) {

//                    Log.e(
//                        TAG,
//                        "onResponseert data: " + i + "   " + response.body()!!.getData()?.get(i)?.date
//                    )

                        max_dollar = response.body()!!.getData()?.get(i)?.close as Float

                        if (max_dollar < (response.body()!!.getData()?.get(i)?.close as Float)) {
                            max_dollar = response.body()!!.getData()?.get(i)?.close as Float
                        }

                        timestamp.add(getTimestamps(response.body()!!.getData()?.get(i)?.date as String))
                        dollars.add(response.body()!!.getData()?.get(i)?.close as Float)
                    }

                    var p = response.body()!!.getChangedValue()

                    if (p != null) {
                        if (p > 0.0) {//
                            percentage_month.setText("(+"+response.body()!!.getChangedValue()+"% "+activity!!.resources.getString(R.string.since_last_30_days)+")")
                            //percentage_month.setText(response.body()!!.getChangeInEltSinceLastMonth())
                            percentage_month.setTextColor(resources.getColor(R.color.green))
                        } else {
                            //percentage_month.setText(response.body()!!.getChangeInEltSinceLastMonth())
                            percentage_month.setText("(+"+response.body()!!.getChangedValue()+"% "+activity!!.resources.getString(R.string.since_last_30_days)+")")
                            percentage_month.setTextColor(resources.getColor(R.color.red))
                        }
                    }

                    var closePrices: MutableList<Entry> = ArrayList()

                    for (i in 0 until response.body()!!.getData()!!.size) {
                        closePrices.add(Entry(timestamp.get(i), dollars.get(i)))
                    }

//                    closePrices = closePrices.subList(closePrices.size, closePrices.size)

//                    if (graphTimeSpan.equals("one_month")) {
//                        closePrices = closePrices.subList(closePrices.size - 720, closePrices.size)
//                    } else if (graphTimeSpan.equals("three_months")) {
//                        closePrices = closePrices.subList(closePrices.size - 1440, closePrices.size)
//                    } else if (graphTimeSpan.equals("six_months")) {
//                        closePrices = closePrices.subList(closePrices.size - 2160, closePrices.size)
//                    } else {
//                        closePrices = closePrices.subList(closePrices.size - 2160, closePrices.size)
//                    }


                    val xAxis = lineChart.xAxis
                    val currPrice = closePrices[closePrices.size - 1].y
                    var firstPrice = closePrices[0].y

                    for (e in closePrices) {
                        if (firstPrice != 0f) {
                            break
                        } else {
                            firstPrice = e.y
                        }
                    }

                    val difference = currPrice - firstPrice
                    val percentChange = difference / firstPrice * 100
                    setColors(percentChange)

                    val dataSet = setUpLineDataSet(closePrices)
                    dataSet.setDrawFilled(true)
                    dataSet.setDrawHorizontalHighlightIndicator(false)
                    dataSet.setDrawVerticalHighlightIndicator(false)

                    var drawable: Drawable? =
                        ContextCompat.getDrawable(context, R.drawable.gradient)
                    dataSet.setFillDrawable(drawable)

                    val lineData = LineData(dataSet)

                    lineChart.data = lineData
                    lineChart.animateX(800)
                    lineChart.getDescription().setEnabled(false)
                    lineChart.getLegend().setEnabled(false)
                    lineChart.setPinchZoom(false)
                    lineChart.setDoubleTapToZoomEnabled(false)
                    lineChart.setTouchEnabled(true)
                    lineChart.getXAxis().setDrawAxisLine(true)

//                    lineChart.getAxisRight().setEnabled(false)
//                    lineChart.getAxisRight().setDrawAxisLine(true)

                    lineChart.setDrawBorders(true)
                    lineChart.setBorderWidth(0.5F)

                    var rightAxis: YAxis = lineChart.getAxisRight()
                    rightAxis.setAxisMinimum(0f)
                    rightAxis.setAxisMaximum(max_dollar + 1.0f)
                    rightAxis.textColor = resources.getColor(R.color.backgroundColor)

                    lineChart.getXAxis().setDrawLabels(true)

                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    if (closePrices.size < 35) {
                        xAxis.labelCount = 30
                    } else if (closePrices.size < 95) {
                        xAxis.labelCount = 3
                    } else if (closePrices.size < 200) {
                        xAxis.labelCount = 6
                    }

                    val xAxisFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            if (closePrices.size < 35) {
                                lineChart.xAxis.textSize = 7F
                                xAxis.mLabelHeight = 100
                                xAxis.labelRotationAngle = -45F
                                return Conversions.getTimeFormatChartDay(value.toLong().toString())
                            } else if (closePrices.size < 95) {
                                lineChart.xAxis.textSize = 9F
                                xAxis.labelRotationAngle = 0F
                                return Conversions.getTimeFormatChartMonth(value.toLong().toString())
                            } else if (closePrices.size < 200) {
                                lineChart.xAxis.textSize = 9F
                                xAxis.labelRotationAngle = 0F
                                return Conversions.getTimeFormatChartMonth(value.toLong().toString())
                            } else {
                                lineChart.xAxis.textSize = 9F
                                xAxis.labelRotationAngle = 0F
                                return Conversions.getTimeFormatChartMonth(value.toLong().toString())
                            }
                        }
                    }

                    with(xAxis) {
                        valueFormatter = xAxisFormatter
                    }
                    lineChart.xAxis.mLabelRotatedHeight = 50

                    var leftAxis: YAxis = lineChart.getAxisLeft()
                    leftAxis.setAxisMinimum(0f)
                    leftAxis.setAxisMaximum(max_dollar + 1.0f)

                }

                DialogBox.closeDialogE()//21-04-2020

            }

            override fun onFailure(call: Call<JsonListModel>, th: Throwable) {
                DialogBox.closeDialogE()//21-04-2020
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }

    fun setUpLineDataSet(entries: List<Entry>): LineDataSet {
        val dataSet = LineDataSet(entries, "Price")
        dataSet.color = chartBorderColor
        dataSet.fillColor = chartFillColor
        dataSet.setDrawHighlightIndicators(true)
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(false)
        dataSet.setCircleColor(chartBorderColor)
        dataSet.lineWidth = 0.7.toFloat()
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.circleRadius = 1f
        dataSet.highlightLineWidth = 2f
        dataSet.isHighlightEnabled = true
        dataSet.setDrawHighlightIndicators(true)
        dataSet.highLightColor = chartBorderColor // color for highlight indicator
        return dataSet
    }

    private fun getTimestamps(str_date: String): Float {

        val formatter = SimpleDateFormat("yyyy-MM-dd")
        var date: Date? = null
        try {
            date = formatter.parse(str_date) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return java.lang.Float.valueOf(java.lang.Long.toString(date!!.time))
    }

    fun setColors(percentChange: Float) {
        val activity = activity
        if (activity != null) {

            chartFillColor =
                ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            chartBorderColor = ResourcesCompat.getColor(resources, R.color.skyblue, null)
            percentageColor =
                ResourcesCompat.getColor(resources, R.color.green, null)

        }
    }
}
