package com.crypto.eltwallet.activities

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.Drawable
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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
//import com.crypto.eltwallet.BuildConfig
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.UpdateProfileModel
import com.crypto.eltwallet.model.UploadProfileImageModel
import com.crypto.eltwallet.model.UserProfileModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import com.mukesh.countrypicker.CountryPicker
import com.mukesh.countrypicker.CountryPickerListener
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener, CountryPickerListener {//AppCompatActivity

    var tv_gender: TextView?=null
    var gender_spinner: Spinner?=null
    var ll_bob : LinearLayout?=null
    var tv_dob : TextView?=null
    var img_back : ImageView?=null
    var img_profilePic : CircleImageView?=null
    var linearLayout : RelativeLayout?=null
    var progressBarPic : ProgressBar?=null
    var edt_fname : EditText?=null
    var edt_lname : EditText?=null
    var edt_legalName : TextView?=null
    var edt_address1 : EditText?=null
    var edt_address2 : EditText?=null
    var edt_city : EditText?=null
    var edt_state : EditText?=null
    var edt_country : EditText?=null
    var edt_postalCode : EditText?=null
    var btn_update : TextView?=null
    var progressBarUpdate : ProgressBar?=null
    var progressBar : ProgressBar?=null
    var scrollView : ScrollView?=null
    var rl_bottom : RelativeLayout?=null

    var day:Int = 0
    var month:Int = 0
    var year:Int = 0

    var mcalendar = Calendar.getInstance()

    var gender = arrayOf<String>("Male", "Female")

    //image
    private var userChoosenTask: String? = null
    val REQUEST_IMAGE_CAPTURE = 1
    private var mCurrentPhotoPath: String? = null
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
    var userProfileImage:File?=null
    var bitmap: Bitmap?=null
    var filename : String?=""
    var newImagePath :String?= ""

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var user_token: String

    private var countryPicker: CountryPicker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()

        val PERMISSIONS = arrayOf(CAMERA_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION)
        if (!hasPermissions(this@ProfileActivity, PERMISSIONS))
        {
            ActivityCompat.requestPermissions((this@ProfileActivity as Activity?)!!, PERMISSIONS,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
        }
        sharedPreferenceUtil = SharedPreferenceUtil(this@ProfileActivity)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        countryPicker = CountryPicker()

        permssion()
        initXml()
        setOnClickListener()
        setOnItemSelectedListener()
        setAdapter()
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
        tv_gender = findViewById(R.id.tv_gender)
        gender_spinner = findViewById(R.id.gender_spinner)
        ll_bob = findViewById(R.id.ll_bob)
        tv_dob = findViewById(R.id.tv_dob)
        img_profilePic = findViewById(R.id.img_profilePic)
        linearLayout = findViewById(R.id.rl_main)
        progressBarPic = findViewById(R.id.progressBarPic)

        edt_fname = findViewById(R.id.edt_fname)
        edt_lname = findViewById(R.id.edt_lname)
        edt_legalName = findViewById(R.id.edt_legalName)
        edt_address1 = findViewById(R.id.edt_address1)
        edt_address2 = findViewById(R.id.edt_address2)
        edt_city = findViewById(R.id.edt_city)
        edt_state = findViewById(R.id.edt_state)
        edt_country = findViewById(R.id.edt_country)
        edt_postalCode = findViewById(R.id.edt_postalCode)
        btn_update = findViewById(R.id.btn_update)
        progressBarUpdate = findViewById(R.id.progressBarUpdate)

        progressBar = findViewById(R.id.progressBar)
        scrollView = findViewById(R.id.scrollView)
//        rl_bottom = findViewById(R.id.rl_bottom)

        edt_legalName!!.isEnabled=false
        edt_country!!.isEnabled=false

        //progressBar!!.visibility=View.VISIBLE
        scrollView!!.visibility=View.GONE
        btn_update!!.visibility=View.GONE

        if (UtilityPermissions.isNetworkAvailable(this))
        {
            img_back!!.isEnabled=false
           // progressBar!!.visibility=View.VISIBLE
            btn_update!!.visibility=View.GONE

            getUserDetails()
        }
        else
        {
            Snackbar.make(linearLayout!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setOnItemSelectedListener()
    {
        gender_spinner!!.onItemSelectedListener=this
    }

    private fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        tv_gender!!.setOnClickListener(this)
        ll_bob!!.setOnClickListener(this)
        img_profilePic!!.setOnClickListener(this)
        btn_update!!.setOnClickListener(this)
        edt_country!!.setOnClickListener(this)

        countryPicker!!.setListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }
            R.id.tv_gender->
            {
                gender_spinner!!.performClick()
            }

            R.id.ll_bob->
            {
                dob()

            }

            R.id.img_profilePic->
            {
                selectImage()
            }

            R.id.btn_update->
            {
                if (progressBarUpdate!!.isVisible)
                { }
                else
                {
                    DialogBox.hideKeyboard(this@ProfileActivity)
                    validations()
                }
            }

            R.id.edt_country->
            {
                countryPicker!!.show(this@ProfileActivity.getSupportFragmentManager(), "well")
            }
        }
    }

    private fun setAdapter()
    {
        var arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gender)
        gender_spinner!!.setAdapter(arrayAdapter)
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
    {
        if (parent!!.getId() == R.id.gender_spinner) {
            tv_gender!!.setText(gender_spinner!!.getSelectedItem().toString())
        }
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun dob()
    {
        day = mcalendar.get(Calendar.DAY_OF_MONTH)//mcalendar[Calendar.DAY_OF_MONTH]
        year = mcalendar.get(Calendar.YEAR)//mcalendar[Calendar.YEAR]
        month = mcalendar.get(Calendar.MONTH)//mcalendar[Calendar.MONTH]


        val listener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            tv_dob!!.setText(""+year+"-"+ String.format("%02d", monthOfYear + 1)  + "-" +String.format("%02d", dayOfMonth))
        }
         val dpDialog =
            DatePickerDialog(this, R.style.DatePickerDialogTheme, listener, 1990, month, day)//year
        dpDialog!!.datePicker.maxDate = System.currentTimeMillis()
        dpDialog!!.show()
        dpDialog!!.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.colorPrimary))
        dpDialog!!.getButton(DatePickerDialog.BUTTON_NEGATIVE)
            .setBackgroundColor(Color.TRANSPARENT)
        dpDialog!!.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorPrimary))
        dpDialog!!.getButton(DatePickerDialog.BUTTON_POSITIVE)
            .setBackgroundColor(Color.TRANSPARENT)


    }

    private fun selectImage()
    {
        val items = arrayOf<CharSequence>(resources.getString(R.string.take_photo), resources.getString(R.string.choose_fromlib),
            resources.getString(R.string.cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.add_photo))
        builder.setItems(items) { dialogInterface, item ->

            val result: Boolean = UtilityPermissions.checkPermission(this@ProfileActivity)
            if (items[item] == resources.getString(R.string.take_photo))
            {
                userChoosenTask = resources.getString(R.string.take_photo)
                if (result)
                {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val file = File(Environment.getExternalStorageDirectory(), "temp.jpg")
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

                }
            }
            else if (items[item] == resources.getString(R.string.choose_fromlib))
            {
                userChoosenTask = resources.getString(R.string.choose_fromlib)
                if (result)
                {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.select_picture)), PICK_IMAGE_REQUEST)

//                    var intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_GALLERY_IMAGE)

//                    val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    startActivityForResult(galleryIntent, SELECT_GALLERY_IMAGE)//requestCode
                }
            }
            else if (items[item] == resources.getString(R.string.cancel))
            {
                dialogInterface.dismiss()
            }
        }
        builder.show()
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

    //check for camera and storage access permissions
    @TargetApi(Build.VERSION_CODES.M)
    private fun checkMultiplePermissions(permissionCode: Int, context: Context)
    {
        val PERMISSIONS = arrayOf(CAMERA_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION)
        if (!hasPermissions(context, PERMISSIONS))
        {
            ActivityCompat.requestPermissions((context as Activity), PERMISSIONS, permissionCode)
        }
        else
        { // Open your camera here.
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val file = File(Environment.getExternalStorageDirectory(), "temp.jpg")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            progressBarPic!!.visibility=View.VISIBLE
            img_profilePic!!.visibility=View.GONE
            img_back!!.isEnabled=false
            //onSelectFromGalleryResult(data)

            filePath = data.data
            try
            {

                val bitmap2 = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                //img_profilePic!!.setImageBitmap(bitmap2)

                val path = saveImage(bitmap2!!)
                userProfileImage = File(path)
                Log.e("userProfileImage",""+path)

                updateProfilePic("gallery")

            }
            catch (e: IOException)
            {
                e.printStackTrace()
                Snackbar.make(linearLayout!!, e.message!!, Snackbar.LENGTH_LONG).show()
                progressBarPic!!.visibility=View.GONE
                img_back!!.isEnabled=true
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            /*try
            {
                if(mCurrentPhotoPath!=null)
                {
                    Log.e("TAG", "onActivityResult:asdfghj3"+mCurrentPhotoPath)
                    filePath = Uri.parse(mCurrentPhotoPath)
                    val mImageBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, filePath)
                    img_profilePic!!.setImageBitmap(mImageBitmap)

                    userProfileImage = File(mCurrentPhotoPath)

                    Log.e("userProfileImage",""+userProfileImage)
                }


            } catch (e: IOException) {
                e.printStackTrace()
            }*/

            try
            {
                img_profilePic!!.visibility=View.GONE
                progressBarPic!!.visibility=View.VISIBLE
                img_back!!.isEnabled=false

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

                newImagePath = filename
                userProfileImage = File(newImagePath)

                Log.e("userProfileImage",""+userProfileImage)

                //var imageUri:Uri = Uri.fromFile(userProfileImage)
                //Glide.with(this).load(imageUri).into(img_profilePic)

                filePath = Uri.fromFile(userProfileImage)

                updateProfilePic("camera")

            }
            catch (e:Exception)
            {
                Log.e("from_signup", e.toString())
                Snackbar.make(linearLayout!!, e.message!!, Snackbar.LENGTH_LONG).show()
                progressBarPic!!.visibility=View.GONE
                img_back!!.isEnabled=true
            }

        }
    }

    private fun getRealPathFromURI(context:Context, contentUri:Uri):String {
        var cursor: Cursor? = null
        try
        {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
        catch (e:Exception) {
            Log.e("", "getRealPathFromURI Exception : " + e.toString())
            return ""
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close()
            }
        }
    }

    @Throws(IOException::class)
    /*private fun getCaptureImageOutputUri(): Uri?
    {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        //val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val storageDirr = File(Environment.getExternalStorageDirectory(), "temp.jpg")
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        return FileProvider.getUriForFile(this@ProfileActivity,
            BuildConfig.APPLICATION_ID.toString() + ".provider", image)
    }*/

    //
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

    private fun calculateInSampleSize(options:BitmapFactory.Options, actualWidth:Int, actualHeight:Int):Int {
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


    private fun updateProfilePic(img_pic_type:String)
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            var requestFile: RequestBody
            var body: MultipartBody.Part

            if (userProfileImage!=null)
            {
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), userProfileImage)
                body = MultipartBody.Part.createFormData("image",""+userProfileImage!!.getName(),requestFile)
            }
            else
            {
                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "")
                body = MultipartBody.Part.createFormData("image","",requestFile)
            }


            (RetrofitClient.getClient.attemptToUpateProfileImage(user_token, body).enqueue(object :Callback<UploadProfileImageModel> {
                override
                fun onResponse(call: Call<UploadProfileImageModel>, response: Response<UploadProfileImageModel>) {

                    Log.e("Response", response.body().toString())

                    progressBarPic!!.visibility=View.GONE
                    img_profilePic!!.visibility=View.VISIBLE
                    img_back!!.isEnabled=true

                    if (response == null)
                    {
                        Snackbar.make(linearLayout!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code()==200)
                    {
                        if (response.body()!!.getStatuscode()!!.equals("200"))
                        {
                            if(img_pic_type.equals("camera"))
                            {
                                Glide.with(this@ProfileActivity).load(filePath).into(img_profilePic)
                            }
                            else if(img_pic_type.equals("gallery"))
                            {
                                val bitmap2 = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                                img_profilePic!!.setImageBitmap(bitmap2)
                            }
                            Snackbar.make(linearLayout!!, resources.getString(R.string.profile_update_successfully), Snackbar.LENGTH_LONG).show()//response.body()!!.getMessage()!!
                        }
                        else
                        {
                            Snackbar.make(linearLayout!!, response.body()!!.getMessage()!!, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    else if (response.code() == 401)
                    {
                        DialogBox.showError(this@ProfileActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if (response.code() == 202)
                    {
                        DialogBox.showError(this@ProfileActivity, (response.errorBody()!!.string()).toString())
                    }


                }
                override fun onFailure(call: Call<UploadProfileImageModel>, t:Throwable) {
                    t.printStackTrace()

                    progressBarPic!!.visibility=View.GONE
                    img_profilePic!!.visibility=View.VISIBLE
                    img_back!!.isEnabled=true

                    Snackbar.make(linearLayout!!, t.message!!, Snackbar.LENGTH_LONG).show()
                }
            }))
        }
        else
        {
            Snackbar.make(linearLayout!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }
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

    private fun validations()
    {
        //edt_country = findViewById(R.id.edt_country)
        //edt_postalCode = findViewById(R.id.edt_postalCode)

        if (edt_fname!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,resources.getString(R.string.please_enter_first_name),Snackbar.LENGTH_LONG).show()
        }
        /*else if (edt_lname!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter last name",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_legalName!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter full name as per National ID",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_address1!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter address1",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_address2!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter address2",Snackbar.LENGTH_LONG).show()
        }
        else if (tv_dob!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter birthday",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_city!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter city",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_state!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter state",Snackbar.LENGTH_LONG).show()
        }
        else if (edt_country!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(linearLayout!!,"Please enter country",Snackbar.LENGTH_LONG).show()
        }*/
        else
        {
            if (UtilityPermissions.isNetworkAvailable(this))
            {
                img_back!!.isEnabled=false
                //progressBarUpdate!!.visibility=View.VISIBLE
                //btn_update!!.setText("")
                updateUserProfile()
            }
            else
            {
                Snackbar.make(linearLayout!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUserProfile()
    {
        DialogBox.showLoader(this)
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.FIRST_NAME, edt_fname!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.LAST_NAME, edt_lname!!.text.toString().trim())
        //weakHashMap.put(ConstantsRequest.LEGAL_NAME, edt_legalName!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.DOB, tv_dob!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.ADDRESS1, edt_address1!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.ADDRESS2, edt_address2!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.CITY, edt_city!!.text.toString().trim())
        //weakHashMap.put(ConstantsRequest.COUNTRY, edt_country!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.STATE, edt_state!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.POSTAL_CODE, edt_postalCode!!.text.toString().trim())

        (RetrofitClient.getClient.attemptToUpdateProfile(user_token, weakHashMap).enqueue(object :Callback<UpdateProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UpdateProfileModel>, response: Response<UpdateProfileModel>) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@ProfileActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@ProfileActivity, ((response.body() as UpdateProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {
                    Snackbar.make(linearLayout!!, R.string.profile_updated_successfully, Snackbar.LENGTH_LONG).show()//response.body()!!.getMessage()!!
                }

                img_back!!.isEnabled=true
                //progressBarUpdate!!.visibility=View.GONE
                //btn_update!!.setText(R.string.update)
            }

            override fun onFailure(call: Call<UpdateProfileModel>, th: Throwable) {
                DialogBox.closeDialogE()
                img_back!!.isEnabled=true
                //progressBarUpdate!!.visibility=View.GONE
                //btn_update!!.setText(R.string.update)

                Snackbar.make(linearLayout!!, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))

    }

    private fun getUserDetails()
    {
        DialogBox.showLoader(this)
        (RetrofitClient.getClient.attemptToUserProfile(user_token).enqueue(object :Callback<UserProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UserProfileModel>, response: Response<UserProfileModel>) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@ProfileActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@ProfileActivity, ((response.body() as UserProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {

                    progressBarPic!!.visibility=View.VISIBLE
                    Glide.with(this@ProfileActivity).load(response.body()!!.getUserImage())
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>,
                                                      isFirstResource: Boolean): Boolean
                            {
                                progressBarPic!!.visibility=View.GONE
                                return false
                            }

                            override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource,
                                                         isFirstResource: Boolean): Boolean
                            {
                                progressBarPic!!.visibility=View.GONE
                                return false
                            }
                        })
                        .into(img_profilePic)

                    edt_fname!!.setText(response.body()!!.getFirstname())
                    if(!response.body()!!.getLastname().equals(""))
                    {
                        edt_lname!!.setText(response.body()!!.getLastname())
                    }
                    /*if(!response.body()!!.getLegalname().equals(""))
                    {

                    }*/

                    edt_legalName!!.setText(response.body()!!.getLegalname())
                    //edt_legalName!!.setText("sdfdgdfgd fdfg dgfgf dfg fggdfgfdg kjljkjkljkljkljkljkjkjj")

                    edt_address1!!.setText(response.body()!!.getAddress1())
                    edt_address2!!.setText(response.body()!!.getAddress2())
                    edt_city!!.setText(response.body()!!.getCity())
                    edt_state!!.setText(response.body()!!.getState())
                    edt_country!!.setText(response.body()!!.getCountry())
                    edt_postalCode!!.setText(response.body()!!.getPostalcode())
                    tv_dob!!.setText(response.body()!!.getDob())

                    img_back!!.isEnabled=true
                  //  progressBar!!.visibility=View.GONE
                    btn_update!!.visibility=View.VISIBLE
                    scrollView!!.visibility=View.VISIBLE
                }
            }

            override fun onFailure(call: Call<UserProfileModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout!!, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

                img_back!!.isEnabled=true
                //progressBarUpdate!!.visibility=View.GONE
                btn_update!!.setText(R.string.update)

            }
        }))

    }

    override fun onSelectCountry(s: String?, s1: String?, s2: String?, i: Int) {
        Log.e("taggss", "onSelectCountry:"+s+" "+s1+" "+s2+",position "+i)
        edt_country!!.setText(s)
        countryPicker!!.dismiss()
    }
}
