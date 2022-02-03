package model

import android.graphics.drawable.Icon

data class Clima(
    var lugar: Lugar = Lugar(),
    var icon: String? = null,
    var condicionActual: CondicionActual = CondicionActual(),
    var temperatura: Temperatura = Temperatura(),
    var viento: Viento = Viento(),
    var nieve: Nieve = Nieve(),
    var nubes: Nubes = Nubes()
)
