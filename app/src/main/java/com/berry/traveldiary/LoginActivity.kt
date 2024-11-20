package com.berry.traveldiary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityLoginBinding
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.PREF_LOGIN
import com.berry.traveldiary.uitility.CommonUtils.delayedFunction
import com.berry.traveldiary.uitility.CommonUtils.getStringPref
import com.berry.traveldiary.uitility.CommonUtils.setStringPref
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var myDatabase: MyDatabase
    private var isLoginSuccess: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@LoginActivity)


        val loginData = getStringPref(PREF_LOGIN, this)

        if (!TextUtils.isEmpty(loginData)) {/*user is already logged in */
            //redirect to dashboard
            val intent = Intent(this, DrawerActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.ivpwdeye.setOnClickListener {
            if (binding.edtPassword.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.edtPassword.inputType = InputType.TYPE_CLASS_TEXT
                binding.ivpwdeye.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_remove_red_eye_24,
                        theme
                    )
                )
            } else {
                binding.edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivpwdeye.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.baseline_visibility_off_24,
                        theme
                    )
                )
            }
        }


        binding.btnLogIn.setOnClickListener {
            //to prevent multi clicks.
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }

            val userName = binding.edtUserName.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            //text validation.
            if (isValid(it, userName, password)) {
                callLogin(it, userName, password)
            }
        }

        binding.btnSignUp.setOnClickListener {
            it?.apply { isEnabled = false; postDelayed({ isEnabled = true }, 400) }
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }


    private fun callLogin(view: View, userName: String, password: String) {
        //calling in background thread.
        CoroutineScope(Dispatchers.IO).launch {
            if (myDatabase.userDao().isUserExists(userName, password)) {
                isLoginSuccess = true
                //get user data.
                val userData = myDatabase.userDao().getUser(userName)
                //setting data to pref.
                setStringPref(PREF_LOGIN, Gson().toJson(userData), this@LoginActivity)
            } else {
                isLoginSuccess = false
            }
        }.invokeOnCompletion {
            if (isLoginSuccess) {

                //to dismiss keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                runBlocking {
                    delayedFunction()
                }

                val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                CommonUtils.showCustomSnackBar(view, "Invalid Username or password")
            }
        }
    }


    private fun isValid(view: View, userName: String, password: String): Boolean {
        return if (TextUtils.isEmpty(userName)) {
            CommonUtils.showCustomSnackBar(view, "Please enter username")
            false
        } else if (TextUtils.isEmpty(password)) {
            CommonUtils.showCustomSnackBar(view, "Please enter password")
            false
        } else {
            true
        }
    }

}