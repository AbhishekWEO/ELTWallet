package com.crypto.eltwallet.fragments_dashborad


import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.crypto.eltwallet.R
import com.crypto.eltwallet.adapter.NotificationsAdapter
import com.crypto.eltwallet.model.NotificationsModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.pagination.PaginationListener
import com.crypto.eltwallet.pagination.PaginationListener.Companion.PAGE_SIZE
import com.crypto.eltwallet.pagination.PaginationListener.Companion.PAGE_START
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.crypto.eltwallet.utilities.SwipeHelper
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: RelativeLayout
    lateinit var swipeRefresh : SwipeRefreshLayout

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth: String

    lateinit var recycler_view: RecyclerView
    private val arrayList = ArrayList<NotificationsModel.DataBean>()
    lateinit var no_notifications : TextView
    lateinit var notificationsAdapter: NotificationsAdapter

    lateinit var skeletonScreen: SkeletonScreen
    //pagination
    var page_number : Int = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val context = context as Activity
        sharedPreferenceUtil = SharedPreferenceUtil(context)
        input_auth = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        linearLayout = view.findViewById(R.id.linearLayout)
        recycler_view = view.findViewById(R.id.recycler_view)
        no_notifications = view.findViewById(R.id.no_notifications)

        //pagination

        setPagination()


        //end

        skeletonScreen = Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
            .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()


        attemptToNotificationsList()
        callSwipe(view)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        swipeRefresh.setOnRefreshListener{

            notificationsAdapter.clear()

            isLastPage = false

            isLoading = true
            page_number = PAGE_START
            skeletonScreen = Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
                .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

            attemptToNotificationsList()

        }

        return view
    }

    private fun setPagination()
    {
        recycler_view.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recycler_view.setLayoutManager(layoutManager)


        notificationsAdapter = NotificationsAdapter(ArrayList(), activity)
        recycler_view.adapter = notificationsAdapter

        recycler_view.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems()
            {
                isLoading = true
                page_number++
                attemptToNotificationsList()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })


    }

    private fun callSwipe(view2: View) {

        val context = context as Activity

//        this.preferenceUtil = SharedPreferenceUtil(activity)
        recycler_view = view2.findViewById(R.id.recycler_view) as RecyclerView
//        this.clear_all = view2.findViewById(R.id.clear_all) as TextView
//        this.clear_all.setOnClickListener(this)
        object : SwipeHelper(context, recycler_view) {

            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                list: MutableList<UnderlayButton>
            ) {
                list.add(
                    UnderlayButton(resources.getString(R.string.delete), 0, Color.parseColor("#FF3C30"),
                        object : UnderlayButtonClickListener {
                            override fun onClick(pos: Int) {

                                DialogBox.showNotificationClear(context, arrayList.get(pos).id.toString(), pos, input_auth, linearLayout, arrayList, notificationsAdapter)

                            }
                        })
                )
            }
        }.attachSwipe()
    }


    fun attemptToNotificationsList() {

//        skeletonScreen =
//            Skeleton.bind(this.recycler_view).adapter(recycler_view.adapter).shimmer(true).angle(20)
//                .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

        (RetrofitClient.getClient.attemptToNotificationsList(input_auth, PAGE_SIZE,page_number).enqueue(object :
            Callback<NotificationsModel> {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<NotificationsModel>, response: Response<NotificationsModel>) {

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

                    var size : Int = response.body()!!.getNotificationtList()!!.size

                    //val notificationList = ArrayList<NotificationsModel.DataBean>()

                    if(size == 0)
                    {

                        /*no_notifications.visibility = View.VISIBLE
                        recycler_view.visibility = View.GONE*/

                        if (page_number == 1)
                        {
                            no_notifications.visibility = View.VISIBLE
                            recycler_view.visibility = View.GONE
                        }
                        else
                        {
                            notificationsAdapter.emptyData()
                            page_number--
                        }

                        isLastPage = true

                    }
                    else
                    {
                        if (page_number == 1)
                        {
                            skeletonScreen.hide()
                        }

                        no_notifications.visibility = View.GONE
                        recycler_view.visibility = View.VISIBLE

                        for (i in 0 until size)
                        {

                            Log.e(TAG, "onResponseert data: " + i + "   " + response.body()!!.getNotificationtList()?.get(i)?.id)
                            Log.e("response", response.body().toString())

                            var notificationsModel = NotificationsModel.DataBean()

                            notificationsModel.id = response.body()!!.getNotificationtList()?.get(i)?.id
                            notificationsModel.userid_s = response.body()!!.getNotificationtList()?.get(i)?.userid_s
                            notificationsModel.userid_r = response.body()!!.getNotificationtList()?.get(i)?.userid_r
                            notificationsModel.address_from = response.body()!!.getNotificationtList()?.get(i)?.address_from
                            notificationsModel.address_to = response.body()!!.getNotificationtList()?.get(i)?.address_to
                            notificationsModel.amt = response.body()!!.getNotificationtList()?.get(i)?.amt
                            notificationsModel.status = response.body()!!.getNotificationtList()?.get(i)?.status
                            notificationsModel.transaction_response = response.body()!!.getNotificationtList()?.get(i)?.transaction_response
                            notificationsModel.created = response.body()!!.getNotificationtList()?.get(i)?.created

                            arrayList.add(notificationsModel)
                            //notificationList.add(notificationsModel)

                        }

                        //notificationsAdapter = NotificationsAdapter(arrayList, context)
                        //recycler_view.adapter = notificationsAdapter


                        if (page_number !== PAGE_START) notificationsAdapter.removeLoading()
                        notificationsAdapter.addItems(arrayList)

                        if(!isLastPage)
                        {
                            notificationsAdapter.addLoading()
                        }


                    }
                    isLoading = false
                }


            }

            override fun onFailure(call: Call<NotificationsModel>, th: Throwable) {

                if(swipeRefresh.isRefreshing){
                    swipeRefresh.isRefreshing = false
                }

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG)
                    .show()

            }
        }))
    }
}
