package br.edu.ifsp.dmo2.healthsmartapp.sensor
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ContadorDePassosManager (private val context: Context,
private val onStepUpdate: (steps: Int) -> Unit): SensorEventListener
{

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var baseStepCount = -1f

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (baseStepCount == -1f) {
                baseStepCount = it.values[0]
            }
            val steps = (it.values[0] - baseStepCount).toInt()
            onStepUpdate(steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}