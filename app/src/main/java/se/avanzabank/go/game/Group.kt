package se.avanzabank.go.game

data class Group(val color: Player, val positions: Set<Position>){
    init{
        require(!positions.isEmpty())
    }

    operator fun plus(group: Group): Group {
        require(this.color == group.color)
        return Group(this.color, this.positions.plus(group.positions))
    }
}