package se.avanzabank.go.game

sealed class Action(){
    abstract var player: Player
}
data class Pass(override var player: Player): Action()
data class Play(val pos: Position, override var player: Player): Action()