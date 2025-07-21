package br.edu.ifsp.dmo2.healthsmartapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.widget.Toast
import br.edu.ifsp.dmo2.healthsmartapp.HomeActivity
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivityExerciseBinding
import br.edu.ifsp.dmo2.healthsmartapp.helper.ContadorDePassosHelper
import br.edu.ifsp.dmo2.healthsmartapp.helper.GiroscopioHelper
import br.edu.ifsp.dmo2.healthsmartapp.model.Workout
import br.edu.ifsp.dmo2.healthsmartapp.firebase.database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseActivity : AppCompatActivity(){

    private lateinit var binding: ActivityExerciseBinding

    private lateinit var sensorManager: SensorManager
    private lateinit var passosHelper: ContadorDePassosHelper
    private lateinit var giroHelper: GiroscopioHelper

    private var currentSteps: Int = 0
    private var isRunning = false

    private var startTime = 0L
    private var elapsedTime = 0L
    private var timerRunning = false

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        passosHelper = ContadorDePassosHelper(this) { passos ->
            currentSteps = passos
            binding.stepsEditText.setText(passos.toString())
        }

        giroHelper = GiroscopioHelper(this) {
            if (!isRunning) {
                startTimer()
                isRunning = true
                Toast.makeText(this, "Treino iniciado via giroscópio!", Toast.LENGTH_SHORT).show()
            }
        }

        setupListeners()
    }


    private fun setupListeners(){

        binding.startButton.setOnClickListener {
            if (!isRunning) {
                startTimer()
                isRunning = true
                binding.startButton.isEnabled = false
                Toast.makeText(this, "Treino iniciado manualmente!", Toast.LENGTH_SHORT).show()
            }
        }


        binding.pauseButton.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
                isRunning = false
                binding.startButton.isEnabled = true
                Toast.makeText(this, "Treino pausado", Toast.LENGTH_SHORT).show()
            } else resumeTimer()
        }

        binding.finishButton.setOnClickListener {
            stopTimer()
            saveWorkout()
            binding.startButton.isEnabled = false
            binding.pauseButton.isEnabled = false
            binding.finishButton.isEnabled = false
        }


        binding.back.setOnClickListener {
            launchHome()
        }


    }

    override fun onResume() {
        super.onResume()
        passosHelper.start()
        giroHelper.start()
    }

    override fun onPause() {
        super.onPause()
        passosHelper.stop()
        giroHelper.stop()
    }

    private fun startTimer() {
        startTime = SystemClock.elapsedRealtime()
        timerRunning = true
        binding.timeTextView.post(timerRunnable)
        binding.pauseButton.text = "Pausar"
    }


    private fun pauseTimer() {
        elapsedTime += SystemClock.elapsedRealtime() - startTime
        timerRunning = false
        binding.timeTextView.removeCallbacks(timerRunnable)
        binding.pauseButton.text = "Retomar"
    }

    private fun resumeTimer() {
        startTime = SystemClock.elapsedRealtime()
        timerRunning = true
        binding.timeTextView.post(timerRunnable)
        binding.pauseButton.text = "Pausar"
    }

    private fun stopTimer() {
        if (timerRunning) {
            elapsedTime += SystemClock.elapsedRealtime() - startTime
        }
        timerRunning = false
        binding.timeTextView.removeCallbacks(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            val totalTime = elapsedTime + (SystemClock.elapsedRealtime() - startTime)
            val seconds = (totalTime / 1000) % 60
            val minutes = (totalTime / 1000) / 60
            binding.timeTextView.text = String.format("%02d:%02d", minutes, seconds)
            if (timerRunning) binding.timeTextView.postDelayed(this, 1000)
        }
    }

    private fun saveWorkout() {
        val userId = auth.currentUser?.uid ?: return
        val timeInMin = (elapsedTime + (SystemClock.elapsedRealtime() - startTime)) / 60000.0
        val calories = estimateCalories(currentSteps, timeInMin)


        val workout = Workout(
            userId = userId,
            steps = currentSteps,
            durationMinutes = timeInMin,
            estimatedCalories = calories,
            timestamp = System.currentTimeMillis()
        )

        database.saveWorkout(
            workout,
            onSuccess = {
                Toast.makeText(this, "Treino salvo!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Erro ao salvar treino", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun estimateCalories(steps: Int, time: Double): Double {
        return (steps * 0.04) + (time * 3)
    }

    private fun launchHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}