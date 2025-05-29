package fr.upjv.project_android_ccm
import android.Manifest


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import fr.upjv.project_android_ccm.ui.activity.ConnectionActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, ConnectionActivity::class.java)
        startActivity(intent)

        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ),
            1
        )
    }
}
