package se.avanzabank.go

import android.databinding.ObservableField
import se.avanzabank.go.game.Game
import se.avanzabank.go.game.Player
import se.avanzabank.go.game.Position

data class GoViewModel(private val game: Game) : GameChangeListener {
    var blackPrisoners = ObservableField(game.blackPrisoners)
    var whitePrisoners = ObservableField(game.whitePrisoners)
    var player = ObservableField(playerDesc())
    var gameOver = ObservableField(game.gameOver)
    val playHandler = object: PlayHandler {
        override fun play(pos: Position) {
            game.play(pos)
        }
    }

    val playSource = object: PlaySource {
        override fun get(): Array<Array<Player>> {
            return game.board.boardState
        }

        override fun isGameOver(): Boolean {
            return game.gameOver
        }
    }

    fun pass(){
        game.pass()
    }

    fun getSize(): Int{
        return game.board.size
    }

    override fun gameChanged(game: Game) {
        blackPrisoners.set(game.blackPrisoners)
        whitePrisoners.set(game.whitePrisoners)
        player.set(playerDesc())
        gameOver.set(game.gameOver)
    }

    private fun playerDesc(): String {
        return when (game.activePlayer) {
            Player.BLACK -> "Black"
            Player.WHITE -> "White"
            else -> ""
        }
    }

    interface PlayHandler {
        fun play(pos: Position)
    }

    interface PlaySource {
        fun get(): Array<Array<Player>>
        fun isGameOver(): Boolean
    }
}
