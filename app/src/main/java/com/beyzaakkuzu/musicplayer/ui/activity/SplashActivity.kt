package com.beyzaakkuzu.musicplayer.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.beyzaakkuzu.musicplayer.R
import com.beyzaakkuzu.musicplayer.util.Constants.REQ_CODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQ_CODE)
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(3000L)
                Intent(this@SplashActivity, MainActivity::class.java).also {
                    startActivity(it)
                    this@SplashActivity.finish()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            REQ_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        Intent(this@SplashActivity, MainActivity::class.java).also {
                            this@SplashActivity.finish()
                            startActivity(it)
                        }
                    }
                } else {
                    Toast.makeText(
                        this@SplashActivity,
                        "Please Grant all permissions to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                this.finishAffinity()
            }

            else -> {
                Toast.makeText(this@SplashActivity, "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
                this.finish()
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        var hasAllPermissions = true
        for (permission in permissions) {
            val res = this.checkCallingOrSelfPermission(permission)
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}