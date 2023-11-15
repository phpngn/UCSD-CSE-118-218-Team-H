/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package io.averwald.sensors.presentation

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import io.averwald.sensors.R
import io.averwald.sensors.presentation.theme.SensorsTheme
import java.util.function.Consumer


class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var heartrateTextView: TextView
    private lateinit var accelerationTextView: TextView
    private lateinit var stepsTextView: TextView
    private lateinit var updateRunnable: Runnable
    private var updateCount = 0
    private var heartRate: Float = 0.0f
    private var acceleration: Float = 0.0f

    private var dataPoints = ArrayList<Float>()

    fun getSensorManager(): SensorManager {
        return getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensors: List<Sensor> = getSensorManager().getSensorList(Sensor.TYPE_ALL)

        val arrayList = ArrayList<String>()

        for (sensor in sensors) {
            arrayList.add(sensor.name)
        }

        arrayList.forEach(Consumer { n: String? ->
            if (n != null) {
                Log.d("sensor", n)
            }
        })

        ActivityCompat.requestPermissions(
            this, listOf(Manifest.permission.BODY_SENSORS).toTypedArray(), 123
        )

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        val accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_FASTEST)

        setContentView(R.layout.layout)
        heartrateTextView = findViewById(R.id.heartRateValue)
        accelerationTextView = findViewById(R.id.accelerationValue)
        stepsTextView = findViewById(R.id.stepsValue)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.values?.lastIndex == 0) {
            // HEARTRATE
            Log.d("heartrate", p0?.values?.get(0).toString())
            heartRate = p0?.values?.get(0)!!
            heartrateTextView.text = p0?.values?.get(0).toString()
            //accelerationTextView.text = acceleration.toString()
        } else {
            // ACCELERATION
            // val totalAcceleration = p0?.values?.get(0)?.plus(p0?.values?.get(1)!!)?.plus(p0?.values?.get(2)!!)
            updateCount++
            val totalAcceleration = Math.sqrt(
                Math.pow(p0?.values?.get(0)?.toDouble()!!, 2.0)
                    .plus(Math.pow(p0?.values?.get(1)?.toDouble()!!, 2.0))
                    .plus(Math.pow(p0?.values?.get(2)?.toDouble()!!, 2.0))
            ).toFloat()
            Log.d("acceleration", totalAcceleration.toString())
            acceleration = totalAcceleration!!
            accelerationTextView.text = acceleration.toString()

            dataPoints.add(totalAcceleration)
            Log.d("dataPoints", dataPoints.lastIndex.toString())

            if(updateCount > 100) {
                stepsTextView.text = countSteps(rollingAverage(dataPoints.toTypedArray(), 20), 12.0).toString()
            }
        }
    }

    fun rollingAverage(datapoints: Array<Float>, windowSize: Int): DoubleArray {
        if (windowSize <= 0 || windowSize > datapoints.size) {
            throw IllegalArgumentException("Invalid window size")
        }

        val result = DoubleArray(datapoints.size - windowSize + 1)

        for (i in 0 until result.size) {
            var sum = 0.0
            for (j in 0 until windowSize) {
                sum += datapoints[i + j]
            }
            result[i] = sum / windowSize
        }

        return result
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun countSteps(datapoints: DoubleArray, threshold: Double): Int {
        var steps = 0
        var isPeak = false

        for (i in 1 until datapoints.size - 1) {
            val prev = datapoints[i - 1]
            val current = datapoints[i]
            val next = datapoints[i + 1]

            if (current > prev && current > next) {
                if (!isPeak && current >= threshold) {
                    steps++
                    isPeak = true
                }
            } else {
                isPeak = false
            }
        }

        return steps
    }
}

@Composable
fun WearApp(greetingName: String) {
    SensorsTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.secondary,
        text = greetingName,
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}