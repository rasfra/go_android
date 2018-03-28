package se.avanzabank.go.game

import se.avanzabank.go.GameChangeListener
import java.util.*
import kotlin.collections.HashSet

class Game(val board: Board)  {
    var activePlayer = Player.BLACK
    var whitePrisoners = 0
    var blackPrisoners = 0
    val plays = LinkedList<Action>()
    var gameOver = false
    val gameChangeListeners = HashSet<GameChangeListener>()

    fun addListener(listener: GameChangeListener){
        gameChangeListeners.add(listener)
    }

    fun inactivePlayer(): Player {
        return when(activePlayer){
            Player.WHITE -> Player.BLACK
            Player.BLACK -> Player.WHITE
            Player.NONE -> Player.NONE
        }
    }

    fun isValidPlay(pos: Position): Boolean{
        if(gameOver){
            println("Game is over, can't play")
            return false
        }
        if(!board.isEmpty(pos)){
            println("Position is taken already")
            return false
        }
        val liberties = libertiesOfNewPlay(pos).size
        val prisonersTaken = prisonersOfNewPlay(pos).size

        println("is valid play? new group has $liberties liberties and $prisonersTaken enemy stones will be taken")
        return liberties > 0 || prisonersTaken > 0
    }

    private fun libertiesOfNewPlay(pos: Position): Set<Position> {
        val directLiberties = board.surroundingPositions(pos)
                .filter { board.getPlayerAt(it) == Player.NONE }
        return board.getConnectedGroups(pos)
                .filter { board.getPlayerAt(it.positions.first()) == activePlayer }
                .flatMap { board.getLiberties(it) } // Liberties of all surrounding groups of this color
                .minus(pos) // Remove this play as a liberty
                .plus(directLiberties) // Add liberties from the new play
                .toSet()
    }

    private fun prisonersOfNewPlay(pos: Position): Set<Position> {
        return board.getConnectedGroups(pos) // Find all opposite groups touching this play
                .filter { board.getPlayerAt(it.positions.first()) == inactivePlayer() }
                .map { Group(it.color, it.positions) } // Remove this play as a liberty for them
                .filter{ board.getLiberties(it).minus(pos).isEmpty() } // Dead groups
                .flatMap { it.positions }.toSet()
    }

    fun play(pos: Position): Boolean {
        if(isValidPlay(pos)) {
            println("Playing $pos as $activePlayer")
            board.set(pos, activePlayer)
            val deadStones = prisonersOfNewPlay(pos)
            deadStones.forEach { board.set(it, Player.NONE) }
            if (activePlayer == Player.BLACK) {
                println("${deadStones.size} prisoners taken for black")
                blackPrisoners += deadStones.size
            } else if (activePlayer == Player.WHITE) {
                println("${deadStones.size} prisoners taken for white")
                whitePrisoners += deadStones.size
            }
            plays.add(Play(pos, activePlayer))
            swapPlayer()
            notifyListeners()
            return true
        }else {
            return false
        }
    }

    // Returns true if game over
    fun pass(): Boolean{
        print("$activePlayer pass.")
        if(plays.lastOrNull() != null && plays.last is Pass){
            gameOver()
            return true
        }
        plays.add(Pass(activePlayer))
        swapPlayer()
        notifyListeners()
        return false
    }

    private fun gameOver() {
        gameOver = true
        notifyListeners()
    }

    private fun swapPlayer(){
        activePlayer = inactivePlayer()
    }

    fun score(player: Player): Int {
        return when(player){
            Player.BLACK -> blackPrisoners
            Player.WHITE -> whitePrisoners
            Player.NONE -> throw IllegalArgumentException("Invalid player")
        }
    }

    fun notifyListeners(){
        gameChangeListeners.forEach{it.gameChanged(this)}
    }
}

