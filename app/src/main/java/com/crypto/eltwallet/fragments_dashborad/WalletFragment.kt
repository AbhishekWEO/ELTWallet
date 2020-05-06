package com.crypto.eltwallet.fragments_dashborad

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.ImportWalletActivity
import com.crypto.eltwallet.adapter.WalletListAdapter
import com.crypto.eltwallet.model.EltCurrentValueModel
import com.crypto.eltwallet.model.WalletListModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class WalletFragment : Fragment() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: RelativeLayout

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth: String

    lateinit var recycler_view: RecyclerView
    private val arrayList = ArrayList<WalletListModel.DataBean>()

    lateinit var add_view : ImageView
    lateinit var arrowup: ImageView
    private var isRotated = true

    lateinit var viewgrp_add: RelativeLayout
    lateinit var create_wallet: RelativeLayout
    lateinit var import_wallet: RelativeLayout

    var dollar_price: Double = 0.0

    lateinit var skeletonScreen: SkeletonScreen
    lateinit var no_wallets : TextView
    lateinit var swipeRefresh: SwipeRefreshLayout


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_list, container, false)

        val context = context as Activity

        no_wallets = view.findViewById(R.id.no_wallets)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        linearLayout = view.findViewById(R.id.linearLayout)
        add_view = view.findViewById(R.id.add_view)
        add_view.setOnClickListener(){
            DialogBoxInput.showCreateWallet(context)
//            rotate()
        }
        arrowup = view.findViewById(R.id.arrowup)

        viewgrp_add = view.findViewById(R.id.viewgrp_import_create)
        create_wallet = view.findViewById(R.id.create_wallet)
        create_wallet.setOnClickListener{
//            DialogBoxInput.showInput(context)
        }

        import_wallet = view.findViewById(R.id.import_wallet)
        import_wallet.setOnClickListener(){
            val intent = Intent(context, ImportWalletActivity::class.java)
            startActivity(intent)
        }

        this.create_wallet.setClickable(false)
        this.import_wallet.setClickable(false)

        recycler_view = view.findViewById(R.id.recycler_view)
        recycler_view.setLayoutManager(LinearLayoutManager(context))

        sharedPreferenceUtil = SharedPreferenceUtil(context)
        input_auth =
            sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        skeletonScreen = Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
                .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

//        attemptToEltCurrentValue()

        swipeRefresh.setOnRefreshListener {
            attemptToEltCurrentValue()
        }

        return view
    }


    override fun onResume() {
        super.onResume()

        attemptToEltCurrentValue()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun rotate() {
        var f = 135.0f
        if (this.isRotated) {
//            TransitionManager.beginDelayedTransition(this.viewgrp_add, Rotate())
            val imageView = add_view
            if (!this.isRotated) {
                f = 0.0f
            }
            imageView.setRotation(f)
            this.isRotated = !this.isRotated

            this.viewgrp_add.animate().translationY(100.0f)
            this.arrowup.animate().translationY(10.0f)
            this.create_wallet.setClickable(true)
            this.import_wallet.setClickable(true)
            return
        }

        val imageView2 = this.add_view
        if (!this.isRotated) {
            f = 0.0f
        }
        imageView2.setRotation(f)

        this.isRotated = !this.isRotated
        this.viewgrp_add.animate().translationY(-150.0f)
        this.arrowup.animate().translationY(29.0f)
        this.create_wallet.setClickable(false)
        this.import_wallet.setClickable(false)
    }


    fun attemptToWalletList() {

//        skeletonScreen =
//            Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
//                .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

        (RetrofitClient.getClient.attemptToWalletList(input_auth).enqueue(object :
            Callback<WalletListModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<WalletListModel>,
                response: Response<WalletListModel>
            ) {
                

                if(swipeRefresh.isRefreshing){
                    swipeRefresh.isRefreshing = false
                }

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401 || response.body()!!.getStatusCode() == 401)
                {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                }
                else if(response.code()==200)
                {
                    arrayList.clear()

                    var size : Int = response.body()!!.getWalletList()!!.size

                    if(size == 0) {

                        no_wallets.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE
                    }
                    else
                    {
                        no_wallets.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE

                        for (i in 0 until size) {

                            Log.e(TAG, "onResponseert data: " + i + " " + response.body()!!.getWalletList()?.get(i)?.id)

                            var walletListModel = WalletListModel.DataBean()

                            walletListModel.id = response.body()!!.getWalletList()?.get(i)?.id
                            walletListModel.walletName = response.body()!!.getWalletList()?.get(i)?.walletName
                            walletListModel.walletAddress = response.body()!!.getWalletList()?.get(i)?.walletAddress
                            walletListModel.walletBal = response.body()!!.getWalletList()?.get(i)?.walletBal

                            arrayList.add(walletListModel)

                        }
                        Log.e("dollars_price", dollar_price.toString())

                        recycler_view.adapter = WalletListAdapter(arrayList, context, dollar_price)
                    }
                }


            }

            override fun onFailure(call: Call<WalletListModel>, th: Throwable) {

                if(swipeRefresh.isRefreshing){
                    swipeRefresh.isRefreshing = false
                }

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }


    fun attemptToEltCurrentValue() {

        val context = context as Activity

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
                    DialogBox.showError(context, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(context, (response.body() as EltCurrentValueModel).message)
                }
                else if(response.code() == 200)
                {
                    dollar_price = (response.body() as EltCurrentValueModel).current_value

                    attemptToWalletList()
                }

            }

            override fun onFailure(call: Call<EltCurrentValueModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

}