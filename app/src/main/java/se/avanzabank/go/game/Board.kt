package se.avanzabank.go.game

class Board(val size: Int) {
    val VALID_SIZES = arrayOf(9, 13, 19)
    var boardState: Array<Array<Player>> = emptyBoard()
        private set

    init {
        require(VALID_SIZES.contains(size), { "Invald board size: $size, must be in 9, 13, 19" })
        println("creating board with size $size")
    }

    fun isEmpty(pos: Position): Boolean {
        return getPlay(pos) == Player.NONE
    }

    fun getPlay(pos: Position) = boardState[pos.y][pos.x]

    fun getPlayerAt(pos: Position): Player {
        return getPlay(pos)
    }

    fun set(pos: Position, state: Player) {
        boardState[pos.y][pos.x] = state
    }

    fun getConnectedGroups(pos: Position): Set<Group> {
        return surroundingPositions(pos).map { getGroup(it) }.toSet()
    }

    // Recursive for fun
    /*
    fun getGroup(pos: Position): Group{
        return buildGroup(pos, HashSet(), getPlayerAt(pos))
    }

    private fun buildGroup(pos: Position, visited: MutableSet<Position>, color: Player): Group{
        visited.add(pos)
        return surroundingPositions(pos)
                .filter { !visited.contains(it) }
                .filter { getPlayerAt(it) == color }
                .map { buildGroup(it, visited, color) }
                .fold(Group(color, HashSet<Position>().plus(pos))) { agg, g ->  agg + g}
    }*/

    fun getGroup(start: Position): Group {
        val targetColor = getPlayerAt(start)
        val result = mutableSetOf<Position>()

        val visited = mutableSetOf<Position>()
        val queue = mutableListOf<Position>()
        queue.add(start)

        while (!queue.isEmpty()) {
            val current = queue.removeAt(0)

            // already checked this cell?
            if (visited.contains(current)) continue
            else visited.add(current)

            // is this cell the color we are looking for?
            if (getPlayerAt(current) == targetColor) result.add(current)
            else continue

            // add neighbours to the queue
            queue.addAll(surroundingPositions(current))
        }

        return Group(targetColor, result)
    }

    fun getLiberties(group: Group): Set<Position> {
        return group.positions.flatMap { surroundingPositions(it) }.toSet()
                .filter { getPlayerAt(it) == Player.NONE }.toSet()
    }

    fun surroundingPositions(pos: Position): Collection<Position> {
        return listOf(above(pos), rightOf(pos), below(pos), leftOf(pos)).filterNotNull()
    }

    fun above(pos: Position): Position? = if (pos.y < size - 1) Position(pos.x, pos.y + 1) else null
    fun below(pos: Position): Position? = if (pos.y > 0) Position(pos.x, pos.y - 1) else null
    fun leftOf(pos: Position): Position? = if (pos.x > 0) Position(pos.x - 1, pos.y) else null
    fun rightOf(pos: Position): Position? = if (pos.x < size - 1) Position(pos.x + 1, pos.y) else null

    private fun emptyBoard(): Array<Array<Player>> =
            Array(size, {
                Array(size, { Player.NONE })
            })

}