package com.example.leonapplication.tdddemo

internal const val BOARD_SIZE = 3

sealed class Player(val name: String) {
  data object A : Player("A")
  data object B : Player("B")
}

internal fun Player.next() = when (this) {
  Player.A -> Player.B
  Player.B -> Player.A
}

class TicTocToe(boardSize: Int = BOARD_SIZE) {

  private val board = Board.new(boardSize)
  var nextPlayer: Player = Player.A
    private set
  var winner: Player? = null
    private set

  val hasWinner get() = winner != null

  fun play(x: Int, y: Int) {
    if (hasWinner) {
      throw IllegalStateException("Player $winner already win")
    }
    board.checkIllegalMove(x, y)

    board.put(x, y, nextPlayer)

    winner = board.checkIsWin()
    if (hasWinner) return

    nextPlayer = nextPlayer.next()
  }

  fun display() = board.display()
}