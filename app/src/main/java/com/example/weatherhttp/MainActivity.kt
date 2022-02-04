package com.example.weatherhttp

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import data.HttpClientClima
import data.JSONParseClima
import data.PreferenceCiudad
import model.Clima
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*


@Suppress("DEPRECATION")
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
    var imageViewIcon: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ciudadPreferece = PreferenceCiudad(this)

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

        ciudadPreferece.ciudad?.let { renderClimaDatos(it) }

        // funDownloadImage()

    }

    fun renderClimaDatos(ciudad: String) {
        val climaTask = ClimaTask()
        climaTask.execute((ciudad + "&appid=" + "86c43f662d4f86b2b8a54d1ef56b6a98"))
    }

    private fun funDownloadImage(fichero: String?){
        val call: Call<ResponseBody> = RetrofitClient.getClient.downloadFileUseingUrl("$fichero.png")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val bytes: ByteArray = response.body()!!.bytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageViewIcon?.setImageBitmap(bitmap)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                throw IllegalAccessError("Ha surgido un problema")
            }
        })
    }

    @SuppressLint("StaticFieldLeak")
    private inner class ClimaTask : AsyncTask<String, Void, Clima>() {

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg p0: String?): Clima {
            val datos = HttpClientClima().getWeatherData(p0[0])
            clima = JSONParseClima.getWeather(datos)!!

            clima.icon = clima.condicionActual.icono
            funDownloadImage(clima.icon)

            return clima
        }

        @SuppressLint("SetTextI18n")
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

    fun mostrarDialog(){
       val builder = AlertDialog.Builder(this)
       builder.setTitle("Cambiar Ciudad")

       val ponerCiudad = EditText(this)
       ponerCiudad.inputType = InputType.TYPE_CLASS_TEXT
       ponerCiudad.hint = "Merida,MX"
       builder.setView(ponerCiudad)
       builder.setPositiveButton("OK") {
           dialogInterface, i ->
                val ciudadPreferencia = PreferenceCiudad(this)
                ciudadPreferencia.ciudad = ponerCiudad.text.toString()

                val ciudadNueva = ciudadPreferencia.ciudad
                ciudadNueva?.let { renderClimaDatos(it) }

       }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.menuCambiar -> mostrarDialog()
        }

        return super.onOptionsItemSelected(item)
    }
}