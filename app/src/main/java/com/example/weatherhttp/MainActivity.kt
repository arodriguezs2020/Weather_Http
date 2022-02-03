package com.example.weatherhttp

import Util.Utils
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import data.HttpClientClima
import data.JSONParseClima
import model.Clima
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.IOException
import java.io.InputStream
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var clima = Clima()
    var textViewCiudad: TextView? = null
    var textViewTemp: TextView? = null
    var textViewHumedad: TextView? = null
    var textViewPresion: TextView? = null
    var textViewViento: TextView? = null
    var textViewSunset: TextView? = null
    var textViewSunrise: TextView? = null
    var textViewUpdate: TextView? = null
    var textViewNube: TextView? = null
    var imageViewIcon: ImageView? = null;


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
        imageViewIcon = findViewById(R.id.imageViewIcon)

        renderClimaDatos("Villahermosa,MX")
    }

    fun renderClimaDatos(ciudad: String) {
        val climaTask = ClimaTask()
        climaTask.execute(*arrayOf(ciudad + "&appid=" + "86c43f662d4f86b2b8a54d1ef56b6a98"))
    }

    private inner class DescargarImagenAsync : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg p0: String?): Bitmap {
            return descargarImagen(p0[0] as String)
        }

        override fun onPostExecute(result: Bitmap?) {
            imageViewIcon?.setImageBitmap(result)
        }

        fun descargarImagen(codigo : String) : Bitmap {

            val cliente = DefaultHttpClient()
            val getRequest = HttpGet(Utils.ICON_URL + codigo + ".png")

            try {
                val response = cliente.execute(getRequest)

                val statusCodigo = response.statusLine.statusCode

                if (statusCodigo != HttpStatus.SC_OK) {
                    Log.e("DescargaImagen", "Error: " + statusCodigo)
                    return null!!
                }

                val entity = response.entity

                if (entity != null) {
                    var inputStream : InputStream? = entity.content

                    val bitmap : Bitmap = BitmapFactory.decodeStream(inputStream)
                    return bitmap
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null!!
        }
    }

    private inner class ClimaTask : AsyncTask<String, Void, Clima>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: String?): Clima {
            val datos = HttpClientClima().getWeatherData(p0[0])
            clima = JSONParseClima.getWeather(datos)!! // Falla aquí porque da null. No me coge los datos de la API

            clima.icon = clima.condicionActual.icono

            DescargarImagenAsync().execute(clima.icon)

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
            textViewNube?.text =
                "Nube: " + clima.condicionActual.condicion + "(" + clima.condicionActual.descripcion + ")"
        }
    }
}