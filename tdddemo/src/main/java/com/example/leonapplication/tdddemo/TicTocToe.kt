package com.example.leonapplication.tdddemo

sealed class Player(val name: String) {
  data object A : Player("A")
  data object B : Player("B")
}

internal fun Player.next() = when (this) {
  Player.A -> Player.B
  Player.B -> Player.A
}

class TicTocToe {

  private val board = emptyBoard()
  var nextPlayer: Player = Player.A
    private set
  var winner: Player? = null
    private set

  val hasWinner get() = winner != null

  fun play(x: Int, y: Int) {
    if (hasWinner) {
      throw IllegalStateException("Player $winner already win")
    }
    checkIllegalMove(x, y)

    board[x][y] = nextPlayer

    winner = checkIsWin()
    if (hasWinner) return

    nextPlayer = nextPlayer.next()
  }

  private fun checkIsWin(): Player? {
    var checkWinner: Player? = board.checkIfAllLineIsSamePlayer()
    if (checkWinner != null) return checkWinner

    val convertedLine = board.switchColumnToLine()
    checkWinner = convertedLine.checkIfAllLineIsSamePlayer()
    if (checkWinner != null) return checkWinner

    checkWinner = board.checkXLinedIsSamePlayer()
    if (checkWinner != null) return checkWinner

    return null
  }

  private fun checkIllegalMove(x: Int, y: Int) {
    if (x !in 0..2) {
      throw RuntimeException("x outside!")
    }
    if (y !in 0..2) {
      throw RuntimeException("y outside!")
    }
    if (board[x][y] != null) {
      throw IllegalArgumentException("position has been taken, ($x,$y)")
    }
  }

  fun display(): String {
    val sb = StringBuilder()
    board.forEachIndexed { index, lines ->
      lines.forEach { player ->
        sb.append("| ${player?.name ?: " "} ")
      }
      sb.append("|")
      if (index == board.lastIndex) return sb.toString()
      sb.append("\n")
      sb.append("-------------\n")
    }
    return sb.toString()
  }
}