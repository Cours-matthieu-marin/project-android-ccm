package fr.upjv.project_android_ccm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import fr.upjv.project_android_ccm.ui.activity.ConnectionActivity
import fr.upjv.project_android_ccm.ui.activity.RegistrationActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, ConnectionActivity::class.java)
        startActivity(intent)
    }
}
