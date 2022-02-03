package com.example.weatherhttp

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import data.HttpClientClima
import data.JSONParseClima
import model.Clima
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var clima = Clima()
    var textViewCiudad : TextView?= null
    var textViewTemp : TextView?= null
    var textViewHumedad : TextView?= null
    var textViewPresion : TextView?= null
    var textViewViento : TextView?= null
    var textViewSunset : TextView?= null
    var textViewSunrise : TextView?= null
    var textViewUpdate : TextView?= null
    var textViewNube : TextView?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewCiudad = findViewById(R.id.textViewCiudad)
        textViewTemp = findViewById(R.id.textViewTemp)
        textViewHumedad = findViewById(R.id.textViewHumedad)
        textViewPresion = findViewById(R.id.textViewPresion)
        textViewViento = findViewById(R.id.textViewViento)
        textViewSunset = findViewById(R.id.textViewSunset)
        textViewSunrise = findViewById(R.id.textViewSunrise)
        textViewUpdate = findViewById(R.id.textViewUpdate)
        textViewNube = findViewById(R.id.textViewNube)

        renderClimaDatos("Merida,MX")
    }

    fun renderClimaDatos(ciudad: String) {
        val climaTask = ClimaTask()
        climaTask.execute(*arrayOf(ciudad + "&APPID="+ "d0b874ce2d41c297d218205813f2c50d" + "&units=metric"))
    }

    private inner class ClimaTask : AsyncTask<String, Void, Clima>() {

        override fun doInBackground(vararg p0: String?): Clima {

            val datos = HttpClientClima().getWeatherData(p0[0])
            clima = JSONParseClima.getWeather(datos)!!

            return clima
        }

        override fun onPostExecute(result: Clima?) {
            super.onPostExecute(result)

            val formatoFecha = DateFormat.getTimeInstance()
            val amanecer = formatoFecha.format(Date(clima.lugar.amanecer))
            val puesta = formatoFecha.format(Date(clima.lugar.puestaSol))
            val actualizar = formatoFecha.format(Date(clima.lugar.ultimaActualizacion))

            val formatoDecimal = DecimalFormat("#.#")
            val formatoTemp = formatoDecimal.format(clima.condicionActual.temperatura)

            textViewCiudad?.text = clima.lugar.ciudad + "," + clima.lugar.pais
            textViewTemp?.text = "" + formatoTemp + "C"
            textViewHumedad?.text = "Humedad: " + clima.condicionActual.humedad + "%"
            textViewPresion?.text = "Presion: " + clima.condicionActual.presion
            textViewViento?.text = "Viento: " + clima.viento.velocidad + "mps"
            textViewSunset?.text = "Puesta del Sol: " + puesta
            textViewSunrise?.text = "Amanecer: " + amanecer
            textViewUpdate?.text = "Ultima actualización: " + actualizar
            textViewNube?.text = "Nube: " + clima.condicionActual.condicion + "(" + clima.condicionActual.descripcion + ")"
        }

    }
}