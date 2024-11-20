package com.berry.traveldiary

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivitySignupBinding
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.isEmailValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var myDatabase: MyDatabase
    private var isSignUpSuccess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //instance of database.
        myDatabase = MyDatabase.getDatabase(this@SignUpActivity)

        binding.addBtn.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            checkAndCreateUser(it)
        }

        binding.btnSignUp.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun checkAndCreateUser(view: View) {
        val userName = binding.edtUserName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (isValid(view, userName, email, password)) {
            CoroutineScope(Dispatchers.IO).launch {
                if (!myDatabase.userDao().isRecordExists(userName, email)) {
                    // data not exist.
                    //insert data to db
                    insertDataToDatabase(userName, email, password)
                } else {
                    // data already exist.
                    isSignUpSuccess = false
                }
            }.invokeOnCompletion {
                if (isSignUpSuccess) {
                    CommonUtils.showCustomSnackBar(view, "User created Successfully")

                    runBlocking {
                        delayedFunction()
                    }

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    CommonUtils.showCustomSnackBar(view, "User Already exist")
                }
            }
        }

    }

    private suspend fun delayedFunction() {
        delay(1500)
    }

    private suspend fun insertDataToDatabase(
        userName: String,
        email: String,
        password: String
    ) {
        // Create User Object
        val user = User(0, userName, email, password)
        myDatabase.userDao().addUser(user)
        isSignUpSuccess = true

    }


    private fun isValid(view: View, userName: String, email: String, password: String): Boolean {
        return if (TextUtils.isEmpty(userName)) {
            CommonUtils.showCustomSnackBar(view, "Please enter username")
            false
        } else if (TextUtils.isEmpty(email)) {
            CommonUtils.showCustomSnackBar(view, "Please enter email")
            false
        } else if (!isEmailValid(email)) {
            CommonUtils.showCustomSnackBar(view, "Please enter valid email")
            false
        } else if (TextUtils.isEmpty(password)) {
            CommonUtils.showCustomSnackBar(view, "Please enter password")
            false
        } else {
            true
        }
    }

}