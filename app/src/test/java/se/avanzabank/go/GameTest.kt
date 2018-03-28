package se.avanzabank.go

import org.junit.Assert.*
import org.junit.Test
import se.avanzabank.go.game.*

/**
 * Board notation: B == Black, W == White, + == Empty, X == black plays here
 */
class GameTest {

    @Test
    fun blackStarts() {
        val game = Game(Board(9))
        val pos = Position(3, 3)
        game.play(pos)
        assertEquals(Player.BLACK, game.board.getPlayerAt(pos))
    }

    @Test
    fun canPlaceWithoutLibertiesIfTakingPrisoner() {
        val game = Game(Board(9))
        // W X W +
        // B W + +
        // + + + +
        game.apply {
            pass()
            white(0, 0)
            black(0, 1)
            white(1, 1)
            pass()
            white(2, 0)
        }
        // Black can now play at 1,0 to take 0,0
        assertTrue(game.isValidPlay(Position(1, 0)))
    }

    @Test
    fun takePrisoner() {
        val game = Game(Board(9))
        // W X + +
        // B + + +
        // + + + +N
        game.apply {
            black(0, 1)
            white(0, 0)
            black(1, 0)
        }
        assertEquals(1, game.blackPrisoners)
    }

    @Test
    fun connectingGroups() {
        val game = Game(Board(9))
        // + + + + + +
        // B B X B B +
        // + + + + + +
        game.apply {
            black(0, 1)
            pass()
            black(1, 1)
            pass()
            black(3, 1)
            pass()
            black(4, 1)
            pass()
        }
        val g1 = game.board.getGroup(Position(1, 1))
        val g2 = game.board.getGroup(Position(3, 1))

        assertEquals(2, g1.positions.size)

        assertEquals(2, g2.positions.size)

        game.black(2, 1)

        val g3 = game.board.getGroup(Position(1, 1))
        assertEquals(5, g3.positions.size)
    }

    @Test
    fun cyclicGroupDetected() {
        val game = Game(Board(9))
        // + + + + +
        // + B B B +
        // B B + B +
        // + B B B +
        // + + + + +
        val plays = listOf(
                Position(1, 1),
                Position(2, 1),
                Position(3, 1),
                Position(0, 2),
                Position(1, 2),
                Position(3, 2),
                Position(1, 3),
                Position(2, 3),
                Position(3, 3)
        )

        game.apply {
            plays.forEach{
                play(it)
                pass()
            }
        }

        val expected = Group(Player.BLACK, plays.toSet())

        expected.positions.forEach { assertEquals(expected, game.board.getGroup(it)) }
    }

    @Test
    fun mixedGroups() {
        val game = Game(Board(9))
        // + + + + +
        // + W B W +
        // + B B W +
        // + B W B +
        // + + + + +
        val b1 = Position(2, 1)
        val b2 = Position(1, 2)
        val b3 = Position(2, 2)
        val b4 = Position(1, 3)
        val w1 = Position(3, 1)
        val w2 = Position(3, 2)

        game.apply {
            play(b1)
            play(Position(1, 1))
            play(b2)
            play(w1)
            play(b3)
            play(w2)
            play(b4)
            play(Position(2, 3))
            play(Position(3, 3))
        }

        val expectedBlackGroup = Group(Player.BLACK, setOf<Position>(b1, b2, b3, b4))
        val expectedWhiteGroup = Group(Player.WHITE, setOf<Position>(w1, w2))

        expectedBlackGroup.positions.forEach { assertEquals(expectedBlackGroup, game.board.getGroup(it)) }
        expectedWhiteGroup.positions.forEach { assertEquals(expectedWhiteGroup, game.board.getGroup(it)) }
    }

    @Test
    fun gameOver() {
        val game = Game(Board(9))
        assertFalse(game.pass())
        assertTrue(game.pass())
    }

    @Test
    fun playTriggersGameChangedEvent() {
        val game = Game(Board(9))
        var changed = false
        game.addListener(object : GameChangeListener {
            override fun gameChanged(game: Game) {
                changed = true
            }
        })
        game.black(0, 0)
        assertTrue(changed)

    }

    fun Game.black(x: Int, y: Int) {
        require(activePlayer == Player.BLACK)
        play(Position(x, y))
    }

    fun Game.white(x: Int, y: Int) {
        require(activePlayer == Player.WHITE)
        play(Position(x, y))
    }
}
