package com.crypto.eltwallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.BaseModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class KycVerification5UploadingActivity : AppCompatActivity() {

    var rl_main : RelativeLayout?=null

    var doc_id : String=""
    var country_name : String=""
    var legal_name : String=""
    var user_token : String=""
    var front_file_path : String=""
    var back_file_path : String=""
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    var frontImageFile:File?=null
    var backImageFile:File?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_verification5_uploading)

        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()


        initXml()
    }

    private fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)

        getSetData()
    }

    private fun getSetData()
    {
        doc_id = intent.getStringExtra(ConstantsRequest.DOC_ID)
        country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)
        legal_name = intent.getStringExtra(ConstantsRequest.LEGAL_NAME)
        front_file_path = intent.getStringExtra(ConstantsRequest.FRONT_FILE_PATH)
        back_file_path = intent.getStringExtra(ConstantsRequest.BACK_FILE_PATH)

        frontImageFile = File(front_file_path)
        backImageFile = File(back_file_path)

        uploadeDocs()
    }

    private fun uploadeDocs()
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            var requestFileFront: RequestBody
            var bodyFront: MultipartBody.Part

            var requestFileBack: RequestBody
            var bodyBack: MultipartBody.Part

            if (frontImageFile!=null)
            {
                requestFileFront = RequestBody.create(MediaType.parse("multipart/form-data"), frontImageFile)
                bodyFront = MultipartBody.Part.createFormData("kycdocfront",frontImageFile!!.getName(),requestFileFront)
            }
            else
            {
                requestFileFront = RequestBody.create(MediaType.parse("multipart/form-data"), "")
                bodyFront = MultipartBody.Part.createFormData("kycdocfront","",requestFileFront)
            }

            if (backImageFile!=null && !back_file_path.isEmpty())
            {
                requestFileBack = RequestBody.create(MediaType.parse("multipart/form-data"), backImageFile)
                bodyBack = MultipartBody.Part.createFormData("kycdocback",backImageFile!!.getName(),requestFileBack)
            }
            else
            {
                requestFileBack = RequestBody.create(MediaType.parse("multipart/form-data"), "")
                bodyBack = MultipartBody.Part.createFormData("kycdocback","",requestFileBack)
            }

            var docid = RequestBody.create(MultipartBody.FORM, ""+doc_id)
            var countryname = RequestBody.create(MultipartBody.FORM,country_name)
            var legalname = RequestBody.create(MultipartBody.FORM,legal_name)

            RetrofitClient.getClient.uploadsDocs(user_token, bodyFront, bodyBack,docid,countryname,legalname).enqueue(object :
                Callback<BaseModel> {
                override
                fun onResponse(call: Call<BaseModel>, response: Response<BaseModel>) {

                    Log.e("Response", response.body().toString())

                    if (response == null)
                    {
                        DialogBox.showErrorDialog(this@KycVerification5UploadingActivity, resources.getString(R.string.response_null))
                    }
                    else if(response.code()==200)
                    {
                        if ((response.body() as BaseModel).statuscode.equals("200"))
                        {
                            var intent = Intent(this@KycVerification5UploadingActivity,KycVerification6ThanksActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Snackbar.make(rl_main!!, (response.body() as BaseModel).message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    else if (response.code() == 401)
                    {
                        DialogBox.showErrorDialog(this@KycVerification5UploadingActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if (response.code() == 202)
                    {
                        DialogBox.showErrorDialog(this@KycVerification5UploadingActivity, (response.errorBody()!!.string()).toString())
                    }


                }
                override fun onFailure(call: Call<BaseModel>, t:Throwable) {
                    t.printStackTrace()
                    //Snackbar.make(rl_main!!, t.message!!, Snackbar.LENGTH_LONG).show()
                    DialogBox.showErrorDialog(this@KycVerification5UploadingActivity, t.message!!)
                }
            })

        }
        else
        {
            Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}
