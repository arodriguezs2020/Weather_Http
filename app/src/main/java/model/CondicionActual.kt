package model

data class CondicionActual(var weatherId: Int = 0,
                           var condicion: String? = null,
                           var descripcion: String? = null,
                           var icono: String? = null,
                           var presion: Float = 0.toFloat(),
                           var humedad: Float = 0.toFloat(),
                           var maxTemp: Float = 0.toFloat(),
                           var minTemp: Float = 0.toFloat(),
                           var temperatura: Double = 0.toDouble())
