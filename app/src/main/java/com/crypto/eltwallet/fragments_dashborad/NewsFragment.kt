package com.crypto.eltwallet.fragments_dashborad

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crypto.eltwallet.R
import com.crypto.eltwallet.adapter.NewsAdapter
import com.crypto.eltwallet.model.NewsList
import com.crypto.eltwallet.model.UserProfileModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.crypto.eltwallet.utilities.UtilityPermissions
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NewsFragment : Fragment() {

    private var rl_main : RelativeLayout?=null
    private var tv_noNewsFound : TextView?=null
    private var rv_newsList : RecyclerView?=null
    private val newsList = ArrayList<NewsList.Datum>()

    lateinit var skeletonScreen: SkeletonScreen
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    var user_token : String=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_news, container, false)

        sharedPreferenceUtil = SharedPreferenceUtil(activity!!)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        initXml(view)
        return view
    }

    fun initXml(view: View)
    {
        rl_main = view.findViewById(R.id.rl_main)
        tv_noNewsFound = view.findViewById(R.id.tv_noNewsFound)
        rv_newsList  = view.findViewById(R.id.rv_newsList)

        rv_newsList!!.setLayoutManager(LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL ,false))

        skeletonScreen = Skeleton.bind(this.rv_newsList).adapter(rv_newsList!!.adapter).shimmer(true).angle(20)
            .frozen(false).duration(1200).count(10).load(R.layout.item_skeleton_wallet).show()

        if (activity!=null)
        {
            if (UtilityPermissions.isNetworkAvailable(activity!!))
            {
                getNews()
                getUserDetails()//checking for user authentication
            }
            else
            {
                Snackbar.make(rl_main!!, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }

        }

    }

    fun getUserDetails()
    {
        RetrofitClient.getClient.attemptToUserProfile(user_token).enqueue(object :Callback<UserProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UserProfileModel>, response: Response<UserProfileModel>) {

                if(response == null)
                {
                    //Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    //DialogBox.showError(activity!!, ((response.body() as UserProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {

                }

            }

            override fun onFailure(call: Call<UserProfileModel>, th: Throwable) {

                //Snackbar.make(rl_main!!, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        })
    }

    private fun getNews() {

        if (!UtilityPermissions.isNetworkAvailable(activity!!))
        {
            Snackbar.make(rl_main!!, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            return
        }

        (RetrofitClient.getClient.getNewsList()
            .enqueue(object : Callback<NewsList> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<NewsList>, response: Response<NewsList>)
                {
                    if (response == null)
                    {
                        Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if (response.errorBody() != null)
                    {
                        try
                        {
                            Snackbar.make(rl_main!!, response.errorBody()!!.string(), Snackbar.LENGTH_LONG).show()
                        }
                        catch (e: IOException)
                        {
                            e.printStackTrace()
                        }
                    }
                    else if (response.isSuccessful())
                    {
                        if (activity==null){}
                        else
                        {
                            newsList.clear()
                            newsList.addAll(response.body()!!.getData()!!)
                            if(newsList.size>0)
                            {
                                tv_noNewsFound!!.visibility=View.GONE

                                var adapter = NewsAdapter(activity!!, newsList)
                                rv_newsList!!.adapter=adapter//NewsAdapter(context!!, newsList)
                            }
                            else
                            {
                                tv_noNewsFound!!.visibility=View.VISIBLE
                            }
                        }



                    }
                    else
                    {
                        try {
                            DialogBox.showError(
                                activity!!,
                                (response.errorBody()!!.string()).toString()
                            )
                        } catch (e2: IOException) {
                            e2.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<NewsList?>, th: Throwable)
                {
                    DialogBox.showError(activity!!, th.message!!)
                }
            }))
    }

}
