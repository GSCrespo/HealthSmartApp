package br.edu.ifsp.dmo2.healthsmartapp.helper

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ContadorDePassosHelper(
    private val context: Context,
    private val onStepUpdate: (steps: Int) -> Unit
) : SensorEventListener {

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var stepCount = 0


    private var lastAcceleration = 0f
    private var lastStepTime = 0L

    // limitando os limiares p contar os passos
    private val STEP_THRESHOLD = 11f
    private val STEP_DELAY_NS = 250_000_000L

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        stepCount = 0
        lastAcceleration = 0f
        lastStepTime = 0L
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]


            val acceleration = sqrt(x * x + y * y + z * z)

            val now = System.nanoTime()

           // filtro de pico por usar acelerometro
            if (acceleration > STEP_THRESHOLD && lastAcceleration <= STEP_THRESHOLD) {
                if (now - lastStepTime > STEP_DELAY_NS) {
                    stepCount++
                    onStepUpdate(stepCount)
                    lastStepTime = now
                }
            }
            lastAcceleration = acceleration
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
