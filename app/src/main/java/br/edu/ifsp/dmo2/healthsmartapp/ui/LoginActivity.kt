package br.edu.ifsp.dmo2.healthsmartapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.dmo2.healthsmartapp.R
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivityLoginBinding
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        checkUser()
    }

    private fun setupListeners(){

        binding.loginButton.setOnClickListener{
            handleLogin()
        }

        binding.signupButton.setOnClickListener {
            launchSignUp()
        }


    }


    private fun checkUser(){

        if(firebaseAuth.currentUser != null){
            launchExercise()
        }
    }


    private fun handleLogin(){
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        launchExercise()
                    } else {
                        Toast.makeText(this, "Erro na validação do login", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this, "Preencha os dois campos", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun launchExercise(){
        startActivity(Intent(this, ExerciseActivity::class.java))
        finish()
    }

}