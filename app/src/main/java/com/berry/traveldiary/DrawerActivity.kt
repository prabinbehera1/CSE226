package com.berry.traveldiary

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.berry.traveldiary.databinding.ActivityDrawerBinding
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.PREFERENCES
import com.berry.traveldiary.uitility.CommonUtils.getStringPref
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson


class DrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding
    private lateinit var userData: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)


        // Set the color for the Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.loginThemeColor, theme)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val headerLayout: View = navView.getHeaderView(0)


        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                val img = getStringPref(CommonUtils.PREF_USER_IMG, this@DrawerActivity)
                if (img != null) {
                    if (img.isNotEmpty()) {
                        headerLayout.findViewById<ShapeableImageView>(R.id.imageView)
                            .setImageURI(img.toUri())
                    }
                }
            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })


        val loginData = getStringPref(CommonUtils.PREF_LOGIN, this)
        if (!TextUtils.isEmpty(loginData)) {
            userData = Gson().fromJson(loginData, User::class.java)
            headerLayout.findViewById<TextView>(R.id.tvName).text = userData.username
            headerLayout.findViewById<TextView>(R.id.tvEmail).text = userData.email
        }


        binding.clLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Log Out")
                .setMessage(resources.getString(R.string.str_logout))
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    dialog.dismiss()
                    callLogout()
                }
                .show()
        }


    }

    private fun callLogout() {
        val preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        //redirect to login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}