package br.edu.ifsp.dmo2.healthsmartapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.dmo2.healthsmartapp.R
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivitySignUpBinding
import com.google.android.ads.mediationtestsuite.activities.HomeActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }


    private fun setupListeners(){

        binding.buttonSignUp.setOnClickListener{
            handleSignUp()
        }

        //binding.buttonHome.setOnClickListener {
            //launchHome()
        //}
    }


    private fun handleSignUp(){
        val email = binding.textEmail.text.toString()
        val password = binding.textPassword.text.toString()
        val confirmPassword = binding.textPasswordConfirm.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password){
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        launchHome()
                    } else {
                        Toast.makeText(this, "Erro na validação do cadastro", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchHome(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

}
