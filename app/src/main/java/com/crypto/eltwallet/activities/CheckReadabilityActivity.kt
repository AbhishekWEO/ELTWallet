package com.crypto.eltwallet.activities

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.makeramen.roundedimageview.RoundedImageView
import java.io.File

class CheckReadabilityActivity : AppCompatActivity(), View.OnClickListener {

    var rl_main : RelativeLayout?=null
    var img_back : ImageView?=null
    var img_id : RoundedImageView?=null
    var tv_retake : TextView?=null
    var tv_readable : TextView?=null

    var doc_id : String=""
    var country_name : String=""
    var legal_name : String=""
    var user_token : String=""
    var front_file_path : String=""
    var back_file_path : String=""
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    var frontImageFile:File?=null
    var backImageFile:File?=null
    private var filePath: Uri? = null
    var docs_name:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_readability)
        supportActionBar?.hide()

        initXml()
        setOnClickListener()

    }

    private fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        img_back = findViewById(R.id.img_back)
        img_id = findViewById(R.id.img_id)
        tv_retake = findViewById(R.id.tv_retake)
        tv_readable = findViewById(R.id.tv_readable)

        getSetData()
    }

    private fun getSetData()
    {
        docs_name = intent.getStringExtra(ConstantsRequest.DOCS_NAME)
        doc_id = intent.getStringExtra(ConstantsRequest.DOC_ID)
        country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)
        legal_name = intent.getStringExtra(ConstantsRequest.LEGAL_NAME)
        front_file_path = intent.getStringExtra(ConstantsRequest.FRONT_FILE_PATH)
        back_file_path = intent.getStringExtra(ConstantsRequest.BACK_FILE_PATH)

        frontImageFile = File(front_file_path)
        backImageFile = File(back_file_path)

        if(intent.getStringExtra(ConstantsRequest.DOCSIDE).equals(resources.getString(R.string.front_side)))
        {
            filePath = Uri.fromFile(frontImageFile)
            checkPicType()
        }
        else if(intent.getStringExtra(ConstantsRequest.DOCSIDE).equals(resources.getString(R.string.back_side)))
        {
            filePath = Uri.fromFile(backImageFile)
            checkPicType()
        }

    }

    private fun checkPicType()
    {
        //Glide.with(this).load(filePath).into(img_id)

        //filePath = Uri.parse(mCurrentPhotoPath)
        val mImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
        img_id!!.setImageBitmap(mImageBitmap)
    }


    private fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        tv_retake!!.setOnClickListener(this)
        tv_readable!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }
            R.id.tv_retake->
            {
                finish()
            }
            R.id.tv_readable->
            {
                if(intent.getStringExtra(ConstantsRequest.DOCSIDE).equals(resources.getString(R.string.front_side)))
                {
                    DialogBox.showUploadBackDocs(this)
                    //readability()
                }
                else if(intent.getStringExtra(ConstantsRequest.DOCSIDE).equals(resources.getString(R.string.back_side)))
                {
                    uploadDocs()
                }

            }
        }
    }

    private fun readability()
    {
        val charSequenceArr = arrayOf<CharSequence>(resources.getString(R.string.yes),
            resources.getString(R.string.skip))

        val builder = AlertDialog.Builder(this)
        //builder.setTitle(resources.getString(R.string.add_photo) as CharSequence)
        builder.setTitle("Do you want to upload back side of document")
        builder.setItems(charSequenceArr, DialogInterface.OnClickListener { dialogInterface, i ->


            if (charSequenceArr[i] == resources.getString(R.string.yes))
            {
                dialogInterface.dismiss()

                var intent = Intent(this,ScanDocsActivity::class.java)
                intent.putExtra(ConstantsRequest.DOCSIDE,resources.getString(R.string.back_side))
                intent.putExtra(ConstantsRequest.DOCS_NAME,docs_name)
                intent.putExtra(ConstantsRequest.DOC_ID,doc_id)
                intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
                intent.putExtra(ConstantsRequest.LEGAL_NAME,legal_name)
                intent.putExtra(ConstantsRequest.FRONT_FILE_PATH,front_file_path)

                startActivity(intent)
            }
            else if (charSequenceArr[i] == resources.getString(R.string.skip))
            {
                dialogInterface.dismiss()
                uploadDocs()
            }

        })
        builder.show()
    }

    fun scanDocs()
    {
        var intent = Intent(this,ScanDocsActivity::class.java)
        intent.putExtra(ConstantsRequest.DOCSIDE,resources.getString(R.string.back_side))
        intent.putExtra(ConstantsRequest.DOCS_NAME,docs_name)
        intent.putExtra(ConstantsRequest.DOC_ID,doc_id)
        intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
        intent.putExtra(ConstantsRequest.LEGAL_NAME,legal_name)
        intent.putExtra(ConstantsRequest.FRONT_FILE_PATH,front_file_path)
        startActivity(intent)
    }

    public fun uploadDocs()
    {
        var intent = Intent(this,KycVerification5UploadingActivity::class.java)
        intent.putExtra(ConstantsRequest.DOC_ID,doc_id)
        intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
        intent.putExtra(ConstantsRequest.LEGAL_NAME,legal_name)
        intent.putExtra(ConstantsRequest.FRONT_FILE_PATH,front_file_path)
        intent.putExtra(ConstantsRequest.BACK_FILE_PATH,back_file_path)
        startActivity(intent)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}
