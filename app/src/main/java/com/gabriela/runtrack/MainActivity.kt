
package com.gabriela.runtrack

import android.app.Activity
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Locale
import kotlin.math.abs

class MainActivity : Activity(), SensorEventListener {

    private lateinit var manager: SensorManager
    private lateinit var datos: TextView
    private var acelerometro: Sensor? = null
    private var giroscopio: Sensor? = null

    private var aceleracion = "Esperando datos..."
    private var giro = "Esperando datos..."
    private var ultimoTiempo = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager = getSystemService(SENSOR_SERVICE) as SensorManager
        acelerometro = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        giroscopio = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val pantalla = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 90, 40, 30)
            setBackgroundColor(Color.rgb(12, 20, 35))
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val titulo = TextView(this).apply {
            text = "RUNTRACK"
            textSize = 32f
            setTextColor(Color.rgb(64, 220, 170))
            gravity = Gravity.CENTER
        }

        val subtitulo = TextView(this).apply {
            text = "Monitor deportivo de movimiento"
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 50)
        }

        datos = TextView(this).apply {
            textSize = 19f
            setTextColor(Color.WHITE)
            setPadding(20, 20, 20, 20)
        }

        pantalla.addView(titulo)
        pantalla.addView(subtitulo)
        pantalla.addView(datos)
        setContentView(pantalla)

        mostrarDatos()
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.let {
            manager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        giroscopio?.let {
            manager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val tiempo = System.currentTimeMillis()


        if (tiempo - ultimoTiempo < 150) return
        ultimoTiempo = tiempo

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val valores = String.format(
            Locale.US, "X: %.2f\nY: %.2f\nZ: %.2f", x, y, z
        )

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                aceleracion = valores + "\n" +
                        if (abs(x) > 3 || abs(y) > 3)
                            "Movimiento detectado"
                        else "Movimiento suave"
            }
            Sensor.TYPE_GYROSCOPE -> {
                giro = valores
            }
        }

        mostrarDatos()
    }

    private fun mostrarDatos() {
        datos.text = """
            ACELERÓMETRO
            ${if (acelerometro != null) "Disponible" else "No disponible"}
            $aceleracion

            GIROSCOPIO
            ${if (giroscopio != null) "Disponible" else "No disponible"}
            $giro
        """.trimIndent()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
