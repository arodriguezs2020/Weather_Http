package model

data class Lugar(var lon: Float = 0.toFloat(),
                 var lat: Float = 0.toFloat(),
                 var amanecer: Long = 0,
                 var puestaSol: Long = 0,
                 var pais: String? = null,
                 var ciudad: String? = null,
                 var ultimaActualizacion: Long = 0)
