package com.berry.traveldiary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.berry.traveldiary.adapter.GalleryAdapter
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityPhotoGallaryBinding
import com.berry.traveldiary.model.Photos
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.NO_IMAGE
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PhotoGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoGallaryBinding
    private lateinit var adapter: GalleryAdapter

    private var imagesList: MutableList<Photos> = mutableListOf()
    private lateinit var myDatabase: MyDatabase
    private var imagePosition: Int = -1
    private var photoId: Int = 0
    private var entryId = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoGallaryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@PhotoGalleryActivity)


        val pEntryId = CommonUtils.getStringPref(CommonUtils.PREF_ENTRY_ID, this)
        if (pEntryId != null) {
            entryId = pEntryId.toInt()
        }



        CoroutineScope(Dispatchers.IO).launch {
            imagesList = myDatabase.photosDao().getPhotosById(entryId)
        }.invokeOnCompletion {
            binding.viewPager
            adapter = GalleryAdapter(imagesList, itemClickListener, itemSaveClickListener)
            binding.viewPager.adapter = adapter

            if (imagesList.size < 1) {
                imagesList.add(Photos(0, entryId, NO_IMAGE, ""))
            }
        }


    }


    private val itemClickListener: GalleryAdapter.OnItemClickListener =
        object : GalleryAdapter.OnItemClickListener {
            override fun monItemClickListener(
                position: Int, imgPos: Int, view: View, forAddBlank: Boolean
            ) {
                imagePosition = position
                photoId = imgPos


                if (forAddBlank) {
                    if (imagesList.size < 4) {
                        imagesList.add(position + 1, Photos(0, entryId, NO_IMAGE, ""))
                        adapter.notifyDataSetChanged()
                        val newPosition = adapter.itemCount - 1
                        binding.viewPager.setCurrentItem(newPosition, true)
                    } else {
                        CommonUtils.showCustomSnackBar(view, "You can't add more than 4 photos.")
                    }
                } else {
                    ImagePicker.with(this@PhotoGalleryActivity)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(
                            1080, 1080
                        )    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
                }
            }

        }


    private val itemSaveClickListener: GalleryAdapter.SaveClickListener =
        object : GalleryAdapter.SaveClickListener {
            override fun mSaveClickListener(
                position: Int,
                photoId: Int,
                imagepath: String,
                desc: String,
                view: View,
                isforDelete: Boolean
            ) {

                if (isforDelete) {
                    imagesList.removeAt(position)
                    GlobalScope.launch(Dispatchers.IO) {
                        myDatabase.photosDao().deleteByPhotoId(photoId)
                    }.invokeOnCompletion {
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                            CommonUtils.showCustomSnackBar(view, "Deleted Successfully..")
                        }
                    }

                } else {

                    if (imagepath == NO_IMAGE) {
                        CommonUtils.showCustomSnackBar(view, "Please add an Image")
                    } else if (desc.isEmpty()) {
                        CommonUtils.showCustomSnackBar(view, "Please Enter Caption")
                    } else {
                        val photos = Photos(position, entryId, imagepath, desc)
                        CoroutineScope(Dispatchers.IO).launch {
                            myDatabase.photosDao().addPhotoData(photos)
                        }.invokeOnCompletion {
                            CommonUtils.showCustomSnackBar(
                                view,
                                "Saved Successfully.."
                            )
                        }
                    }
                }
            }

        }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            if (imagePosition != -1) {
                imagesList[imagePosition].imagePath = uri.toString()
                adapter.notifyDataSetChanged()
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}