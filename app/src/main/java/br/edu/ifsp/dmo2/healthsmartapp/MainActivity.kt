package br.edu.ifsp.dmo2.healthsmartapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.dmo2.healthsmartapp.ui.LoginActivity
import br.edu.ifsp.dmo2.healthsmartapp.ui.SignUpActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }




}