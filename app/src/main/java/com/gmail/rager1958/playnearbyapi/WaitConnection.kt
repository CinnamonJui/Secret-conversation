package com.gmail.rager1958.playnearbyapi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import kotlinx.android.synthetic.main.activity_wait_for_connection.*

class WaitConnection : AppCompatActivity() {
    private lateinit var mApplication: NearByConnection
    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val hasDenial = permissions.map {
            checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
        }.any { isDenied ->
            return@any isDenied
        }

        if (hasDenial) requestPermissions(permissions, 42)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 42) {
            val hasDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
            if (hasDenial) {
                Toast.makeText(
                    this,
                    "We need all this permission to connect with nearby devices",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_for_connection)

        requestPermission()
        mApplication = application as NearByConnection

        startDiscovery.setOnClickListener {
            startActivity(Intent(this, ChatRoom::class.java))
        }
        mApplication.prepareConnectionClient(this)
    }
}
