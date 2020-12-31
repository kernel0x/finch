package com.kernel.finch.common.models

sealed class Position {

    object Bottom : Position()

    object Top : Position()

    class Below(val id: String) : Position()

    class Above(val id: String) : Position()
}
