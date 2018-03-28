package se.avanzabank.go

import se.avanzabank.go.game.Game

interface GameChangeListener {
    fun gameChanged(game: Game)
}