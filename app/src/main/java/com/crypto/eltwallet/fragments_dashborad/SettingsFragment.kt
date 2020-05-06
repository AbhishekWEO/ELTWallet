package com.crypto.eltwallet.fragments_dashborad


import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.AccountSecurityActivity
import com.crypto.eltwallet.activities.IDVerificationActivity
import com.crypto.eltwallet.activities.ProfileActivity
import com.crypto.eltwallet.activities.SplashActivity
import com.crypto.eltwallet.model.GetLangToken
import com.crypto.eltwallet.model.LogOutModel
import com.crypto.eltwallet.model.UserProfileModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.mikhaellopez.circularimageview.CircularImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment(), View.OnClickListener {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout : LinearLayout
    lateinit var img_accSecurity: ImageView
    var switch_notifications : SwitchCompat?=null

    lateinit var rl_personal_information : RelativeLayout
    lateinit var rl_accountSecurity : RelativeLayout
    lateinit var rl_identity_verification : RelativeLayout
    lateinit var rl_push_notifications : RelativeLayout
    lateinit var rl_language : RelativeLayout
    lateinit var rl_currency : RelativeLayout
    lateinit var rl_terms_use : RelativeLayout
    lateinit var rl_privacy_policy : RelativeLayout

    lateinit var progressBarPic : ProgressBar
    lateinit var profile_pic : CircularImageView

    lateinit var logout: TextView
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var input_auth : String



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        val context = context as Activity

        sharedPreferenceUtil = SharedPreferenceUtil(activity!!)

        initXml(view)
        setOnClickListener()
        return view

    }

    fun initXml(rootView: View)
    {
        progressBarPic = rootView.findViewById(R.id.progressBarPic)
        profile_pic = rootView.findViewById(R.id.profile_pic)
        linearLayout = rootView.findViewById(R.id.linearLayout)

        rl_personal_information = rootView.findViewById(R.id.rl_personal_information)
        rl_accountSecurity = rootView.findViewById(R.id.rl_accountSecurity)
        rl_identity_verification = rootView.findViewById(R.id.rl_identity_verification)
        rl_push_notifications = rootView.findViewById(R.id.rl_push_notifications)
        rl_language = rootView.findViewById(R.id.rl_language)
        rl_currency = rootView.findViewById(R.id.rl_currency)
        rl_terms_use = rootView.findViewById(R.id.rl_terms_use)
        rl_privacy_policy = rootView.findViewById(R.id.rl_privacy_policy)

        logout = rootView.findViewById(R.id.logout)
        logout.setOnClickListener {

            //
            DialogBox.showLogoutDialog(activity!!, View.OnClickListener { view ->
                    if (view.id == R.id.yes)
                    {
                        DialogBox.closeDialogE()
                        if (UtilityPermissions.isNetworkAvailable(activity!!))
                        {
                            DialogBox.showLoader(activity!!)
                            attemptToLogOut()
                        }
                        else
                        {
                            Snackbar.make(linearLayout, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                        }
                    }
                    else if (view.id == R.id.no)
                    {
                        DialogBox.closeDialogE()
                    }
                })

        }

        switch_notifications = rootView.findViewById(R.id.switch_notifications)
        setOnCheckedChangeListener()

    }

    private fun setOnCheckedChangeListener()
    {
        if (sharedPreferenceUtil.getBoolean(SharedPreferencesConstants.isFcmEnabled, true)!!)
        {
            switch_notifications!!.isChecked=true
        }
        switch_notifications!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            Log.e("SettingFragment",""+isChecked)
            sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isFcmEnabled,isChecked)
            sharedPreferenceUtil.save();
        })

    }

    fun setOnClickListener()
    {
        rl_personal_information.setOnClickListener(this)
        rl_accountSecurity.setOnClickListener(this)
        rl_identity_verification.setOnClickListener(this)
        rl_language.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.rl_personal_information->
            {
                var intent = Intent(activity, ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.rl_accountSecurity->
            {
                var intent = Intent(activity, AccountSecurityActivity::class.java)
                startActivity(intent)
            }

            R.id.rl_identity_verification->
            {
                var intent = Intent(activity, IDVerificationActivity::class.java)
                startActivity(intent)
            }
            R.id.rl_language->
            {
                setLang()
            }
        }
    }


    fun attemptToLogOut() {

        val context = context as Activity

        (RetrofitClient.getClient.attemptToLogOut(input_auth).enqueue(object :
            Callback<LogOutModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<LogOutModel>, response: Response<LogOutModel>)
            {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(context, (response.body() as LogOutModel).message)
                }
                else if(response.code() == 200)
                {
                    var lang = sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "")

                    sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,"")
                    sharedPreferenceUtil.save()

                    sharedPreferenceUtil.clearAll()

                    sharedPreferenceUtil.setString(SharedPreferencesConstants.LANGUAGE, lang!!)
                    sharedPreferenceUtil.save()

                    context.startActivity(Intent(context, SplashActivity::class.java))
                    context.finish()
                }

            }

            override fun onFailure(call: Call<LogOutModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

    override fun onResume()
    {
        super.onResume()

        input_auth = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        if (activity!=null)
        {
            if (UtilityPermissions.isNetworkAvailable(activity!!))
            {
                progressBarPic.visibility=View.VISIBLE
                profile_pic.visibility=View.GONE

                getUserDetails()
            }
            else
            {
                Snackbar.make(linearLayout, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }



    }

    fun getUserDetails()
    {
        val context = context as Activity

        (RetrofitClient.getClient.attemptToUserProfile(input_auth).enqueue(object :Callback<UserProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UserProfileModel>, response: Response<UserProfileModel>) {

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(context, ((response.body() as UserProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {
                    if (activity!=null)
                    {
                        progressBarPic.visibility=View.VISIBLE
                        profile_pic.visibility=View.VISIBLE
                        Glide.with(activity!!.applicationContext).load(response.body()!!.getUserImage())
                            .listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>,
                                                          isFirstResource: Boolean): Boolean
                                {
                                    progressBarPic.visibility=View.GONE
                                    return false
                                }

                                override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource,
                                                             isFirstResource: Boolean): Boolean
                                {
                                    progressBarPic.visibility=View.GONE
                                    return false
                                }
                            })
                            .into(profile_pic)
                    }



                }

            }

            override fun onFailure(call: Call<UserProfileModel>, th: Throwable) {

                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

    //
    private fun setLang()
    {
        val popupMenu = PopupMenu(getActivity(), rl_language)
        popupMenu.getMenu().add(activity!!.resources.getString(R.string.english))
        popupMenu.getMenu().add(activity!!.resources.getString(R.string.chinese))
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                if (menuItem.title.toString().equals(activity!!.resources.getString(R.string.english)) && !sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("English"))
                {
                    DialogBox.showLangDialog(activity!!,activity!!.resources.getString(R.string.do_you_really_want_to_change_lang),
                        View.OnClickListener { view ->
                            if (view.id == R.id.yes)
                            {
                                DialogBox.closeDialogE()

                                DialogBox.showLoader(activity!!)

                                val locale = Locale("en")
                                Locale.setDefault(locale)
                                val config = Configuration()
                                config.locale = locale
                                resources.updateConfiguration(config, resources.displayMetrics)

                                sharedPreferenceUtil.setString(SharedPreferencesConstants.LANGUAGE, "English")
                                sharedPreferenceUtil.save()

                                if (UtilityPermissions.isNetworkAvailable(activity!!))
                                {
                                    getLangAuth()
                                }
                                else
                                {
                                    DialogBox.closeDialogE()
                                    Snackbar.make(linearLayout, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                                }

                                /*startActivity(Intent(activity, SplashActivity::class.java))
                                activity!!.finishAffinity()*/
                            }
                            else if (view.id == R.id.no)
                            {
                                DialogBox.closeDialogE()
                            }
                        })
                }
                else if (menuItem.title.toString().equals(activity!!.resources.getString(R.string.chinese)) && !sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("Chinese"))
                {
                    DialogBox.showLangDialog(activity!!, activity!!.resources.getString(R.string.do_you_really_want_to_change_lang),
                        View.OnClickListener { view ->
                            if (view.id == R.id.yes)
                            {
                                DialogBox.closeDialogE()

                                DialogBox.showLoader(activity!!)

                                val locale = Locale("zh")
                                Locale.setDefault(locale)
                                val config = Configuration()
                                config.locale = locale
                                resources.updateConfiguration(config, resources.displayMetrics)

                                /*//29-04-2020
                                val locale = Locale("zh")
                                Locale.setDefault(locale)
                                val configuration: Configuration = activity!!.getResources().getConfiguration()
                                configuration.setLocale(locale)
                                configuration.setLayoutDirection(locale)
                                activity!!.createConfigurationContext(configuration)
                                //end*/

                                sharedPreferenceUtil.setString(SharedPreferencesConstants.LANGUAGE, "Chinese")
                                sharedPreferenceUtil.save()

                                if (UtilityPermissions.isNetworkAvailable(activity!!))
                                {
                                    getLangAuth()
                                }
                                else
                                {
                                    DialogBox.closeDialogE()
                                    Snackbar.make(linearLayout, activity!!.resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                                }

                                /*startActivity(Intent(activity, SplashActivity::class.java))
                                activity!!.finishAffinity()*/
                            }
                            else if (view.id == R.id.no)
                            {
                                DialogBox.closeDialogE()
                            }
                        })
                }
                return true
            }
        })
    }

    //
    fun getLangAuth()
    {
        RetrofitClient.getClient.getLangAuth(input_auth).enqueue(object :Callback<GetLangToken> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<GetLangToken>, response: Response<GetLangToken>) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(activity!!, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(activity!!, ((response.body() as GetLangToken).message).toString())
                }
                else if(response.code() == 200)
                {
                    if (activity!=null)
                    {
                        sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,response.body()!!.usertoken)
                        sharedPreferenceUtil.save()
                        startActivity(Intent(activity, SplashActivity::class.java))
                        activity!!.finishAffinity()
                    }
                }

            }

            override fun onFailure(call: Call<GetLangToken>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, th.message!!, Snackbar.LENGTH_LONG).show()

            }
        })
    }

}
