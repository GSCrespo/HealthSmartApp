package br.edu.ifsp.dmo2.healthsmartapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.dmo2.healthsmartapp.R
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.dmo2.healthsmartapp.databinding.ActivityExerciseBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseBinding

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var gyroSensor: Sensor? = null

    private var stepsAtStart: Float = -1f
    private var currentSteps: Int = 0
    private var isRunning = false

    private var startTime = 0L
    private var elapsedTime = 0L
    private var timerRunning = false

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        binding.pauseButton.setOnClickListener {
            if (timerRunning) pauseTimer() else resumeTimer()
        }

        binding.finishButton.setOnClickListener {
            stopTimer()
            saveWorkout()
        }
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                if (stepsAtStart == -1f) {
                    stepsAtStart = event.values[0]
                }
                currentSteps = (event.values[0] - stepsAtStart).toInt()
                binding.stepsEditText.setText(currentSteps.toString())
            }
            Sensor.TYPE_GYROSCOPE -> {
                if (!isRunning && Math.abs(event.values[2]) > 3) {
                    startTimer()
                    isRunning = true
                    Toast.makeText(this, "Treino iniciado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

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

        db.collection("workouts")
            .add(workout)
            .addOnSuccessListener {
                Toast.makeText(this, "Treino salvo!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar treino", Toast.LENGTH_SHORT).show()
            }
    }

    private fun estimateCalories(steps: Int, time: Double): Double {
        return (steps * 0.04) + (time * 3)
    }
}