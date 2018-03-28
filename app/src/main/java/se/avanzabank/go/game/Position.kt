package se.avanzabank.go.game

data class Position(val x: Int, val y: Int) {
    init {
        // zero-based indexing
        require(x in 0..18)
        require(y in 0..18)
    }
}