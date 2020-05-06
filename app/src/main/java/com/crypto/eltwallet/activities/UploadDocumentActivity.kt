package com.crypto.eltwallet.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import java.io.*
import java.util.*

class UploadDocumentActivity : AppCompatActivity(), View.OnClickListener {

    var img_back : ImageView?=null
    var rl_main : RelativeLayout?=null
    var rl_frontimage : RelativeLayout?=null
    var tv_frontText : TextView?=null
    var isuploadedfront :ImageView?=null

    var backimage : RelativeLayout?=null
    var tv_backText : TextView?=null
    var isuploadedback :ImageView?=null
    var tv_submit : TextView?=null

    var frontImageSelected : Boolean=false
    var userChoosenTask : String=""

    val REQUEST_IMAGE_CAPTURE = 1
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 71
    val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1
    var CAMERA_PERMISSION = Manifest.permission.CAMERA
    var READ_EXTERNAL_STORAGE_PERMISSION =
        Manifest.permission.READ_EXTERNAL_STORAGE
    var WRITE_EXTERNAL_STORAGE_PERMISSION =
        Manifest.permission.WRITE_EXTERNAL_STORAGE


    var TEMP_PHOTO_FILE_NAME :String= "temp_photo.jpg"
    var mFileTemp:File?=null
    var frontImageFile:File?=null
    var backImageFile:File?=null
    var bitmap: Bitmap?=null
    var filename : String?=""
    var doc_id : String=""
    var country_name : String=""
    var legal_name : String=""
    var user_token : String=""
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    var front_file_path : String=""
    var back_file_path : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)

        supportActionBar?.hide()

        val PERMISSIONS = arrayOf(CAMERA_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION)
        if (!hasPermissions(this@UploadDocumentActivity, PERMISSIONS))
        {
            ActivityCompat.requestPermissions((this@UploadDocumentActivity as Activity?)!!, PERMISSIONS,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
        }
        permssion()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        initXml()
        setOnClickListener()
    }
    private fun permssion()
    {
        if (Build.VERSION.SDK_INT >= 24)
        {
            try
            {
                val m = StrictMode::class.java!!.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            }
            catch (e:Exception) {
                e.printStackTrace()
            }
        }
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mFileTemp = File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)
        }
        else
        {
            mFileTemp = File(getFilesDir(), TEMP_PHOTO_FILE_NAME)
        }
    }


    private fun initXml()
    {
        img_back = findViewById(R.id.img_back)
        rl_main = findViewById(R.id.rl_main)
        rl_frontimage = findViewById(R.id.rl_frontimage)
        tv_frontText = findViewById(R.id.tv_frontText)
        isuploadedfront = findViewById(R.id.isuploadedfront)
        backimage = findViewById(R.id.backimage)
        tv_backText = findViewById(R.id.tv_backText)
        isuploadedback = findViewById(R.id.isuploadedback)
        tv_submit = findViewById(R.id.tv_submit)

        getSetData()
    }

    private fun getSetData()
    {
        if (intent.extras!=null)
        {
            doc_id = intent.getStringExtra(ConstantsRequest.DOC_ID)
            country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)
            legal_name = intent.getStringExtra(ConstantsRequest.LEGAL_NAME)
        }
    }

    private fun setOnClickListener()
    {
        rl_frontimage!!.setOnClickListener(this)
        backimage!!.setOnClickListener(this)
        tv_submit!!.setOnClickListener(this)
        img_back!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }
            R.id.rl_frontimage->
            {
                frontImageSelected = true
                selectImage()
            }
            R.id.backimage->
            {
                frontImageSelected = false
                selectImage()
            }
            R.id.tv_submit->
            {
                validation()
            }
        }
    }

    private fun selectImage()
    {
        val charSequenceArr = arrayOf<CharSequence>(resources.getString(R.string.take_photo),
            resources.getString(R.string.choose_fromlib), resources.getString(R.string.cancel))

        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.add_photo) as CharSequence)
        builder.setItems(charSequenceArr, DialogInterface.OnClickListener { dialogInterface, i ->

            val checkPermission: Boolean = UtilityPermissions.checkPermission(this@UploadDocumentActivity)

            if (charSequenceArr[i] == this@UploadDocumentActivity.getResources().getString(R.string.take_photo)) {
                this@UploadDocumentActivity.userChoosenTask =
                    this@UploadDocumentActivity.getResources().getString(R.string.take_photo)
                if (checkPermission) {
                    cameraIntent()
                }
            } else if (charSequenceArr[i] == this@UploadDocumentActivity.getResources().getString(R.string.choose_fromlib)) {
                this@UploadDocumentActivity.userChoosenTask =
                    this@UploadDocumentActivity.getResources().getString(R.string.choose_fromlib)
                if (checkPermission) {
                    this@UploadDocumentActivity.galleryIntent()
                }
            } else if (charSequenceArr[i] == this@UploadDocumentActivity.getResources().getString(R.string.cancel)) {
                dialogInterface.dismiss()
            }
        })
        builder.show()
    }

    private fun cameraIntent()
    {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(Environment.getExternalStorageDirectory(), "temp.jpg")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun galleryIntent()
    {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (permission in permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false
                }
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode != -1) {
            return
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            onSelectFromGalleryResult(data)
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            try
            {
                var fil = File(Environment.getExternalStorageDirectory().toString())
                for (temp in fil.listFiles())
                {
                    if (temp.getName().equals("temp.jpg"))
                    {
                        fil = temp
                        break
                    }
                }
                val bitmapOptions = BitmapFactory.Options()
                Log.e("edit", "new image path is " + fil.getAbsolutePath())
                Log.e("Result Ok", "" + data)
                compressImage(fil.getAbsolutePath())

                //var newImagePath = filename
                //userProfileImage = File(newImagePath)

                if (frontImageSelected)
                {
                    front_file_path = filename!!
                    frontImageFile = File(front_file_path)
                    Log.e("frontImageFile",""+frontImageFile)
                    //filePath = Uri.fromFile(frontImageFile)
                    tv_frontText!!.setText(front_file_path)
                    tv_frontText!!.setTextColor(resources.getColor(R.color.skyblue))
                    isuploadedfront!!.visibility = View.VISIBLE
                }
                else
                {
                    back_file_path = filename!!
                    backImageFile = File(back_file_path)
                    Log.e("backImageFile",""+backImageFile)
                    //filePath = Uri.fromFile(backImageFile)
                    tv_backText!!.setText(back_file_path)
                    tv_backText!!.setTextColor(resources.getColor(R.color.skyblue))
                    isuploadedback!!.visibility = View.VISIBLE
                }

            }
            catch (e:Exception)
            {
                e.printStackTrace()
                Log.e("error",""+e.message)
            }

        }
    }



    private fun onSelectFromGalleryResult(data: Intent?)
    {

        filePath = data!!.data
        try
        {
            val bitmap2 = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            //img_profilePic!!.setImageBitmap(bitmap2)

            val path = saveImage(bitmap2!!)
            //userProfileImage = File(path)
            Log.e("userProfileImage",""+path)

            if (frontImageSelected)
            {
                front_file_path = path
                frontImageFile = File(front_file_path)
                Log.e("frontImageFile",""+frontImageFile)

                tv_frontText!!.text = front_file_path
                tv_frontText!!.setTextColor(resources.getColor(R.color.skyblue))
                isuploadedfront!!.visibility = View.VISIBLE
                return
            }
            else
            {
                back_file_path = path
                backImageFile = File(back_file_path)
                Log.e("backImageFile",""+backImageFile)

                tv_backText!!.text = back_file_path
                tv_backText!!.setTextColor(resources.getColor(R.color.skyblue))
                isuploadedback!!.visibility = View.VISIBLE
            }


        }
        catch (e: IOException)
        {
            e.printStackTrace()
            Log.e("error",""+e.message)
        }

    }


    private fun compressImage(absolutePath:String):String {
        var filePath = getRealPathFromURI1(absolutePath)
        var scaledBitmap:Bitmap ?= null
        var options = BitmapFactory.Options()
        // by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        bitmap = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        // max Height and width values of the compressed image is taken as 816x612
        var maxHeight = 816.0f
        var maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        var maxRatio = maxWidth / maxHeight
        // width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth)
        {
            if (imgRatio < maxRatio)
            {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            }
            else if (imgRatio > maxRatio)
            {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            }
            else
            {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        // setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        // inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false
        // this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try
        {
            // load the bitmap from its path
            bitmap = BitmapFactory.decodeFile(filePath, options)
        }
        catch (exception:OutOfMemoryError) {
            exception.printStackTrace()
        }
        try
        {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        }
        catch (exception:OutOfMemoryError) {
            exception.printStackTrace()
        }
        var ratioX = actualWidth / (options.outWidth).toFloat()
        var ratioY = actualHeight / (options.outHeight).toFloat()
        var middleX = actualWidth / 2.0f
        var middleY = actualHeight / 2.0f
        var scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        var canvas = Canvas(scaledBitmap)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bitmap, middleX - bitmap!!.getWidth() / 2, middleY - bitmap!!.getHeight() / 2, Paint(
            Paint.FILTER_BITMAP_FLAG)
        )
        // check the rotation of the image and display it properly
        var exif: ExifInterface
        try
        {
            exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: " + orientation)
            val matrix = Matrix()
            if (orientation == 6)
            {
                matrix.postRotate(90F)
                Log.d("EXIF", "Exif: " + orientation)
            }
            else if (orientation == 3)
            {
                matrix.postRotate(180F)
                Log.d("EXIF", "Exif: " + orientation)
            }
            else if (orientation == 8)
            {
                matrix.postRotate(270F)
                Log.d("EXIF", "Exif: " + orientation)
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap!!.getWidth(), scaledBitmap!!.getHeight(), matrix,
                true)
        }
        catch (e:IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream?= null
        filename = getFFilename()
        try
        {
            out = FileOutputStream(filename)
            // write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return filename!!
    }

    private fun getRealPathFromURI1(contentURI:String):String {
        val contentUri = Uri.parse(contentURI)
        val cursor = getContentResolver().query(contentUri, null, null, null, null)
        if (cursor == null)
        {
            return contentUri.getPath()
        }
        else
        {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, actualWidth:Int, actualHeight:Int):Int {
        var height = options.outHeight
        var width = options.outWidth
        var inSampleSize = 1
        if (height > actualHeight || width > actualWidth)
        {
            var heightRatio = Math.round(height.toFloat() / actualHeight.toFloat())
            var widthRatio = Math.round(width.toFloat() / actualWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        var totalPixels = (width * height).toFloat()
        var totalReqPixelsCap = (actualWidth * actualHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap)
        {
            inSampleSize++
        }
        return inSampleSize
    }

    private fun getFFilename():String {
        val file = File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images")
        if (!file.exists())
        {
            file.mkdirs()
        }
        val uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg")
        return uriSting.toString()
    }
    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() + "/demonuts_upload")
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun validation()
    {
        if (frontImageFile==null)
        {
            Snackbar.make(rl_main!!,resources.getString(R.string.please_upload_front_side_of_document),Snackbar.LENGTH_LONG).show()
        }
        else
        {
            var intent = Intent(this@UploadDocumentActivity,KycVerification5UploadingActivity::class.java)
            intent.putExtra(ConstantsRequest.DOC_ID,doc_id)
            intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
            intent.putExtra(ConstantsRequest.LEGAL_NAME,legal_name)
            intent.putExtra(ConstantsRequest.FRONT_FILE_PATH,front_file_path)
            intent.putExtra(ConstantsRequest.BACK_FILE_PATH,back_file_path)
            startActivity(intent)
            //uploadeDocs()
        }

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
                bodyFront = MultipartBody.Part.createFormData("kycdocfront",""+frontImageFile!!.getName(),requestFileFront)
            }
            else
            {
                requestFileFront = RequestBody.create(MediaType.parse("multipart/form-data"), "")
                bodyFront = MultipartBody.Part.createFormData("kycdocfront","",requestFileFront)
            }

            if (backImageFile!=null)
            {
                requestFileBack = RequestBody.create(MediaType.parse("multipart/form-data"), backImageFile)
                bodyBack = MultipartBody.Part.createFormData("kycdocback",""+backImageFile!!.getName(),requestFileBack)
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

                    //img_back!!.isEnabled=true

                    if (response == null)
                    {
                        Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code()==200)
                    {
                        if ((response.body() as BaseModel).statuscode.equals("200"))
                        {
                            //Snackbar.make(rl_main!!, (response.body() as BaseModel).message, Snackbar.LENGTH_LONG).show()
                            var intent = Intent(this@UploadDocumentActivity,KycVerification6ThanksActivity::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Snackbar.make(rl_main!!, (response.body() as BaseModel).message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    else if (response.code() == 401)
                    {
                        DialogBox.showError(this@UploadDocumentActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if (response.code() == 202)
                    {
                        DialogBox.showError(this@UploadDocumentActivity, (response.errorBody()!!.string()).toString())
                    }

                }
                override fun onFailure(call: Call<BaseModel>, t:Throwable) {
                    t.printStackTrace()

                    //img_back!!.isEnabled=true
                    Snackbar.make(rl_main!!, t.message!!, Snackbar.LENGTH_LONG).show()
                }
            })

        }
        else
        {
            Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }
    }
}