package com.sd.src.stepcounterapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sd.src.stepcounterapp.R
import com.sd.src.stepcounterapp.activities.MyProfileActivity
import com.sd.src.stepcounterapp.model.profile.Data
import com.sd.src.stepcounterapp.model.profile.UpdateProfileRequest
import com.sd.src.stepcounterapp.network.RetrofitClient
import com.sd.src.stepcounterapp.utils.SharedPreferencesManager
import com.sd.src.stepcounterapp.viewModels.ProfileViewModel
import com.sd.src.stepcounterapp.viewModels.SignInViewModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.backtitlebar.*
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : BaseFragment(), View.OnClickListener {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.maleBttn -> {
                isGenderClicked = false
                selectGender(v)
            }
            R.id.femaleBttn -> {
                isGenderClicked = true
                selectGender(v)
            }
            R.id.img_nav_header -> {
                chooseImage()
            }
            R.id.img_back -> {
                activity?.onBackPressed()
            }
            R.id.saveinfoBttn -> {
                if (validateInputs()) {
                    saveProfile()
                }
            }

            R.id.lbs_wt -> {
                isWtButtonClicked = false
                changeBttnBg(v)
            }
            R.id.kgs_wt -> {
                isWtButtonClicked = true
                changeBttnBg(v)

            }
            R.id.cms_ht -> {
                isHtButtonClicked = true
                changeHtBttnBg(v)

            }
            R.id.fts_ht -> {
                isHtButtonClicked = false
                changeHtBttnBg(v)
            }
        }
    }


    /**
     * validate inputs
     */

    private fun validateInputs(): Boolean {
        if (firstNameEd.text.isEmpty()) {
            Toast.makeText(activity, "Firstname field can't be empty.", Toast.LENGTH_LONG).show()
            return false
        } else if (lastNameEd.text.isEmpty()) {
            Toast.makeText(activity, "Lastname field can't be empty.", Toast.LENGTH_LONG).show()
            return false
        } else if (heightTxt.text.isEmpty()) {
            Toast.makeText(activity, "Height field can't be empty.", Toast.LENGTH_LONG).show()
            return false
        } else if (weightTxt.text.isEmpty()) {
            Toast.makeText(activity, "Weight field can't be empty.", Toast.LENGTH_LONG).show()
            return false
        } else if (mobileEd.text.isEmpty()) {
            Toast.makeText(activity, "Mobile field can't be empty.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * Save profile details
     */

    private fun saveProfile() {

        var w = if (isWtButtonClicked) "Kgs" else "Lbs"
        var h = if (isHtButtonClicked) "Cms" else "Feet"
        showPopupProgressSpinner(true)
        mProfileViewModel.updateProfileData(
            UpdateProfileRequest(
                firstNameEd.text.toString(),
                lastNameEd.text.toString(),
                w,
                gender,
                h,
                dobTxt.text.toString(),
                mobileEd.text.toString().toInt(),
                weightTxt.text.toString().toFloat(),
                SharedPreferencesManager.getUserId(mContext),
                heightTxt.text.toString().toFloat(),
                SharedPreferencesManager.getUpdatedUserObject(mContext).bmi
            )
        )

    }

    private fun chooseImage() {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions(mContext)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Please allow these permissions from settings to use mContext feature",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {

                }
            }).check()
    }

    private var croppedImage: Bitmap? = null
    private val file: File? = null
    private lateinit var mProfileViewModel: ProfileViewModel
    private var mCalendar = Calendar.getInstance()
    var selectedImage: Uri? = null
    private lateinit var fileName: String
    private var imageFileBody: MultipartBody.Part? = null
    private var uri: Uri? = null
    private var isGenderClicked: Boolean = false
    private var userData: Data? = null
    private var gender: String = ""
    private val REQUEST_GALLERY_IMAGE: Int = 111
    private val REQUEST_IMAGE_CAPTURE: Int = 112
    private val REQUEST_CROP_IMAGE: Int = 113
    private lateinit var mViewModel: SignInViewModel
    var isWtButtonClicked: Boolean = false
    var isHtButtonClicked: Boolean = false


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: ProfileFragment

        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        fun newInstance(context: Context): ProfileFragment {
            instance = ProfileFragment()
            mContext = context
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(SignInViewModel::class.java)
        mProfileViewModel = ViewModelProviders.of(activity!!).get(ProfileViewModel::class.java)
        userData = SharedPreferencesManager.getUpdatedUserObject(mContext)
        Picasso.get().load(RetrofitClient.IMG_URL + userData?.image).placeholder(R.drawable.nouser).into(img_nav_header)
        firstNameEd.setText(userData!!.firstName.toString())
        lastNameEd.setText(userData!!.lastName.toString())
        if (SharedPreferencesManager.getUpdatedUserObject(mContext).gender.equals("Male", true)) {
            isGenderClicked = false
            selectGender(maleBttn)
        } else {
            isGenderClicked = true
            selectGender(femaleBttn)
        }
        dobTxt.setText(userData!!.dob.toString())
        heightTxt.setText(userData!!.height.toString())
        weightTxt.setText(userData!!.weight.toString())
        maleBttn.setOnClickListener(this)
        femaleBttn.setOnClickListener(this)
        saveinfoBttn.setOnClickListener(this)
        mobileEd.setText(userData!!.mobile.toString())
        img_nav_header.setOnClickListener(this)
        kgs_wt.setOnClickListener(this)
        lbs_wt.setOnClickListener(this)
        fts_ht.setOnClickListener(this)
        cms_ht.setOnClickListener(this)
        img_back.setOnClickListener(this)
        kgs_wt.performClick()
        cms_ht.performClick()


        dobTxt.setOnTouchListener(object : View.OnTouchListener, DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, month)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= dobTxt.right - dobTxt.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        DatePickerDialog(
                            activity, this, mCalendar
                                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                        return true
                    }
                }
                return false
            }
        })
        mViewModel.getImageResponse()
            .observe(this, androidx.lifecycle.Observer { mImg ->
                showPopupProgressSpinner(false)
            })

        mProfileViewModel.getUpdateProfileResponse().observe(this, androidx.lifecycle.Observer { mData ->
            showPopupProgressSpinner(false)
            if (mData.status == 200) {
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_LONG).show()
                mContext.startActivity(Intent(activity, MyProfileActivity::class.java))

            }
        })

    }


    /**
     * gender selection
     */

    @RequiresApi(Build.VERSION_CODES.M)
    private fun selectGender(v: View) {
        if (v.id == R.id.maleBttn) {
            maleBttn.setBackgroundResource(if (!isGenderClicked) R.drawable.fill else R.drawable.unfill)
            femaleBttn.setBackgroundResource(R.drawable.unfill)
            maleBttn.setTextColor(resources.getColor(R.color.white))
            femaleBttn.setTextColor(resources.getColor(R.color.colorBlack))
            maleBttn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male_w, 0, 0, 0)
            femaleBttn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female_b, 0, 0, 0)
            gender = "male"
        } else if (v.id == R.id.femaleBttn) {
            femaleBttn.setBackgroundResource(if (isGenderClicked) R.drawable.fill else R.drawable.unfill)
            maleBttn.setBackgroundResource(R.drawable.unfill)
            maleBttn.setTextColor(resources.getColor(R.color.colorBlack))
            femaleBttn.setTextColor(resources.getColor(R.color.white))
            femaleBttn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female, 0, 0, 0)
            maleBttn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male, 0, 0, 0)
            gender = "female"
        }
    }


    /**
     * weight units selection
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeBttnBg(v: View) {
        if (v.id == R.id.kgs_wt) {
            lbs_wt.setBackgroundResource(if (isWtButtonClicked) R.drawable.unfill else R.drawable.fill)
            v.setBackgroundResource(if (isWtButtonClicked) R.drawable.fill else R.drawable.unfill)
            kgs_wt.setTextColor(resources.getColor(R.color.white))
            lbs_wt.setTextColor(resources.getColor(R.color.colorBlack))

        } else if (v.id == R.id.lbs_wt) {
            kgs_wt.setBackgroundResource(if (!isWtButtonClicked) R.drawable.unfill else R.drawable.fill)
            v.setBackgroundResource(if (!isWtButtonClicked) R.drawable.fill else R.drawable.unfill)
            kgs_wt.setTextColor(resources.getColor(R.color.colorBlack))
            lbs_wt.setTextColor(resources.getColor(R.color.white))
        }
    }

    /**
     * height units selection
     */

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeHtBttnBg(v: View) {
        if (v.id == R.id.cms_ht) {
            fts_ht.setBackgroundResource(if (isHtButtonClicked) R.drawable.unfill else R.drawable.fill)
            v.setBackgroundResource(if (isHtButtonClicked) R.drawable.fill else R.drawable.unfill)
            cms_ht.setTextColor(resources.getColor(R.color.white))
            fts_ht.setTextColor(resources.getColor(R.color.colorBlack))

        } else if (v.id == R.id.fts_ht) {
            cms_ht.setBackgroundResource(if (!isHtButtonClicked) R.drawable.unfill else R.drawable.fill)
            v.setBackgroundResource(if (!isHtButtonClicked) R.drawable.fill else R.drawable.unfill)
            fts_ht.setTextColor(resources.getColor(R.color.white))
            cms_ht.setTextColor(resources.getColor(R.color.colorBlack))

        }
    }

    fun showImagePickerOptions(context: Context) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo))

        // add a list
        val animals = arrayOf(
            context.getString(R.string.lbl_take_camera_picture),
            context.getString(R.string.lbl_choose_from_gallery)
        )
        builder.setItems(animals) { dialog, which ->
            when (which) {
                0 -> takeCameraImage()
                1 -> chooseImageFromGallery()
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun takeCameraImage() {
        Dexter.withActivity(activity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName = System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName))
                        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun chooseImageFromGallery() {
        Dexter.withActivity(activity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                selectedImage = data!!.data
//                img_nav_header.setImageURI(selectedImage)
                ImageCropFunction()

            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                selectedImage = data!!.data
//                profileImg.setImageURI(selectedImage)
                ImageCropFunction()
            }
            CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                var result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                img_nav_header.setImageURI(result.uri)
                croppedImage = result.bitmap
                uploadImageToServer()
            }

        }
    }

    private fun uploadImageToServer() {
        var requestUserID: RequestBody =
            RequestBody.create(
                MediaType.parse("multipart/form-data"),
                SharedPreferencesManager.getUserId(mContext)!!
            )
        showPopupProgressSpinner(true)
        mViewModel!!.uploadImage(requestUserID, getFiletoServer())
    }


    fun getRealPathFromURI(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor = activity?.managedQuery(contentUri, proj, null, null, null)
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(column_index!!)!!
    }

    /**
     * request body for image
     */


    fun getFiletoServer(): MultipartBody.Part {
        Log.i("Selected", "Cropped" + selectedImage + "!!!" + croppedImage)

        // add another part within the multipart request
        if (croppedImage != null) {
            var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), getFileFromBitmap())
            imageFileBody = MultipartBody.Part.createFormData("image", file!!.name, requestBody)
        }
        return imageFileBody!!
    }


    /**
     * create file from bitmap
     */

    private fun getFileFromBitmap(): File {
        var f = File(context?.cacheDir, "CroppedImg" + System.currentTimeMillis())
        f.createNewFile()
//Convert bitmap to byte array
        var bitmap = croppedImage
        var bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        var bitmapdata = bos.toByteArray();

//write the bytes in file
        var fos = FileOutputStream(f)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return f
    }


    fun ImageCropFunction() {
        context?.let {
            CropImage.activity(selectedImage)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(800, 800)
                .setMaxCropResultSize(1000, 1000)
                .start(it, this)
        }
    }


    private fun updateLabel() {
        var myFormat = "dd/MM/yyyy" //In which you need put here
        var sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        dobTxt.setText(sdf.format(mCalendar.time))
    }


    /**
     * Kgs to Lbs
     */

    fun convertKgsToLbs(kgs: Int): Int {
        var result = kgs * 2.20
        return result.toInt()
    }


    /**
     * Cms to Feet
     */

    fun convertCmsToInch(cms: Int): Int {
        var result = cms * 0.39f
        return result.toInt()
    }

}
