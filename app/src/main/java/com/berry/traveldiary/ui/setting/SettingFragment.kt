package com.berry.traveldiary.ui.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.berry.traveldiary.R
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.FragmentSettingBinding
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.PREF_USER_IMG
import com.berry.traveldiary.uitility.CommonUtils.getStringPref
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var userPasswd: String
    private var userId: Int = -1
    private lateinit var myDatabase: MyDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this)[GalleryViewModel::class.java]

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDatabase = MyDatabase.getDatabase(requireContext())

        val loginData = getStringPref(CommonUtils.PREF_LOGIN, requireContext())
        if (!TextUtils.isEmpty(loginData)) {
            val user: User = Gson().fromJson(loginData, User::class.java)
            userPasswd = user.password
            userId = user.id
        }

        binding.swNotif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                CommonUtils.showCustomSnackBar(buttonView, "Notification Turned ON")
            } else {
                CommonUtils.showCustomSnackBar(buttonView, "Notification Turned OFF")
            }
        }

        binding.tvProfileInfo.setOnClickListener {
            ImagePicker.with(this@SettingFragment)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080, 1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        binding.tvPassword.setOnClickListener {
            showPasswordDialog(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            CommonUtils.setStringPref(PREF_USER_IMG, uri.toString(), requireContext())
            CommonUtils.showCustomSnackBar(
                binding.root.rootView,
                "Profile Image Updated Successfully."
            )

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showPasswordDialog(view: View) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password_input, null)

        val currentPswd = dialogView.findViewById<EditText>(R.id.ectCurrent)
        val newPswd = dialogView.findViewById<EditText>(R.id.ectNew)

        // Create the Material AlertDialog using MaterialAlertDialogBuilder
        MaterialAlertDialogBuilder(requireContext()).setTitle("Change Password").setView(dialogView)
            .setCancelable(false).setPositiveButton("UPDATE") { dialog, which ->

                val cPass = currentPswd.text.toString().trim()
                val nPass = newPswd.text.toString().trim()

                if (cPass.isNotEmpty() && nPass.isNotEmpty()) {
                    if (this.userPasswd.isNotEmpty() && cPass == this.userPasswd) {
                        CoroutineScope(Dispatchers.IO).launch {
                            myDatabase.userDao().updatePassword(userId, nPass)
                        }.invokeOnCompletion {
                            CommonUtils.showCustomSnackBar(
                                view, "Password Updated Successfully."
                            )
                            dialog.dismiss()
                        }
                    } else {
                        CommonUtils.showCustomSnackBar(view, "Current Password Dose not match.")
                    }
                } else {
                    CommonUtils.showCustomSnackBar(view, "Password Can't be empty.")
                }


            }.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }.show()
    }

}