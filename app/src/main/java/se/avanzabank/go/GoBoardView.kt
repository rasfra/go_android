package se.avanzabank.go

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import se.avanzabank.go.game.Game
import se.avanzabank.go.game.Player
import se.avanzabank.go.game.Position


class GoBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs), GameChangeListener {
    private var board: Board? = null
    var size: Int? = null
    var playHandler: GoViewModel.PlayHandler? = null
    var playSource: GoViewModel.PlaySource? = null
    val detector = GestureDetector(this.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            e?.let {
                val coords = Pair(it.x, it.y)
                println("touch on $coords")
                board?.closestPosition(coords)
                        ?.let { playHandler?.play(it) }
            }
            return true
        }
    })

    init {
        // Create a board at the earlist time we have view size
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                size?.let {
                    board = Board(it, width, 0.1f)
                }
            }
        })
    }

    override fun gameChanged(game: Game) {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        board?.apply {
            draw(canvas)
            playSource?.let {
                if (!it.isGameOver()) {
                    drawStones(canvas, it.get())
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        detector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    class Board(val size: Int, val sizeP: Int, val stoneMargin: Float) {
        val BOARD_COLOR = Color.rgb(233, 195, 144)
        val squareLen: Float = sizeP / (size + 1).toFloat()
        val margin = squareLen
        val innerLen = (sizeP - margin * 2)
        val stoneRadius = squareLen * (0.5f - stoneMargin / 2)
        val maxError = 0.25f
        init {
            println("Creating board with res $sizeP")
        }

        fun toCoords(pos: Position): Pair<Float, Float> {
            return Pair(margin + squareLen * pos.x, margin + squareLen * pos.y)
        }

        fun closestPosition(coords: Pair<Float, Float>): Position? {
            // Transform to coordinates in a size x size plane
            val exactX = (coords.first - margin) / squareLen
            val exactY = (coords.second - margin) / squareLen
            // Find closest position by rounding to an integer
            val discX = Math.round(exactX)
            val discY = Math.round(exactY)
            val xOffBy = Math.abs(exactX - discX)
            val yOffBy = Math.abs(exactY - discY)
            return if (xOffBy < maxError && discX in 0 until size
                    && yOffBy < maxError  && discY in 0 until size)
                Position(discX, discY) else null
        }

        fun draw(canvas: Canvas) {
            val paint = Paint()
            paint.isAntiAlias = true;
            paint.color = Color.BLACK;
            paint.style = Paint.Style.FILL_AND_STROKE;
            paint.strokeJoin = Paint.Join.ROUND;
            paint.strokeWidth = 3f;
            canvas.drawColor(BOARD_COLOR)
            // Columns
            (0 until size).forEach {
                val startX = squareLen * it
                val startY = 0f
                val stopX = squareLen * it
                val stopY = innerLen
                drawLineWithMargin(canvas, paint, startX, startY, stopX, stopY)
            }
            // Rows
            (0 until size).forEach {
                val startX = 0f
                val startY = squareLen * it
                val stopX = innerLen
                val stopY = squareLen * it
                drawLineWithMargin(canvas, paint, startX, startY, stopX, stopY)
            }
        }

        fun drawLineWithMargin(canvas: Canvas, paint: Paint, startX: Float, startY: Float, stopX: Float, stopY: Float) {
            canvas.drawLine(
                    margin + startX,
                    margin + startY,
                    margin + stopX,
                    margin + stopY, paint)
        }

        fun drawStones(canvas: Canvas, plays:Array<Array<Player>>) {
            val white = Paint()
            white.color = Color.WHITE;
            val black = Paint()
            black.color = Color.BLACK;

            plays.forEachIndexed {yPos, col -> col.forEachIndexed{ xPos, _ ->
                val(x, y) = toCoords(Position(xPos, yPos))
                val player = plays[yPos][xPos]

                if (player == Player.WHITE) {
                    println("Drawing white stone at $x, $y")
                    canvas.drawCircle(x, y, stoneRadius, white)
                } else if (player == Player.BLACK) {
                    println("Drawing black stone at $x, $y")
                    canvas.drawCircle(x, y, stoneRadius, black)
                }
            } }
        }
    }
}
