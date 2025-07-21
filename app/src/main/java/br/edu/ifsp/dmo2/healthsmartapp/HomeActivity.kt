package br.edu.ifsp.dmo2.healthsmartapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivityHomeBinding
import br.edu.ifsp.dmo2.healthsmartapp.firebase.database
import br.edu.ifsp.dmo2.healthsmartapp.ui.ExerciseActivity
import br.edu.ifsp.dmo2.healthsmartapp.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUser()
        setupListeners()
        carregarResumo()
    }

    private fun setupListeners(){

        binding.btnIrTreinar.setOnClickListener {
            startActivity(Intent(this, ExerciseActivity::class.java))
            finish()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Usuário deslogado!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun carregarResumo() {
        database.loadUserWorkouts(
            onResult = { workouts ->
                val totalTreinos = workouts.size
                val totalPassos = workouts.sumOf { it.steps }
                val totalMinutos = workouts.sumOf { it.durationMinutes }
                val totalCalorias = workouts.sumOf { it.estimatedCalories }

                binding.editTreinos.setText(totalTreinos.toString())
                binding.editPassos.setText(totalPassos.toString())
                binding.editTempo.setText("%.1f".format(totalMinutos))
                binding.editCalorias.setText("%.1f".format(totalCalorias))
            },
            onFailure = {
                Toast.makeText(this, "Erro ao carregar treinos", Toast.LENGTH_SHORT).show()
            }
        )
    }


    private fun checkUser(){

        if(auth.currentUser == null){
            launchLogin()
        }
    }

    private fun launchLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}