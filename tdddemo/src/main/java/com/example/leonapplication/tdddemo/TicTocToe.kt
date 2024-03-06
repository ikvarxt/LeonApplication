package com.example.leonapplication.tdddemo

sealed class Player(val name: String) {
  data object A : Player("A")
  data object B : Player("B")
  data object None : Player("None")
}

class TicTocToe {

  private val board = arrayOf<Array<Player?>>(
    arrayOfNulls(3),
    arrayOfNulls(3),
    arrayOfNulls(3),
  )
  var nextPlayer: Player = Player.A
    private set
  var winner: Player = Player.None
    private set

  fun play(x: Int, y: Int) {
    if (winner != Player.None) {
      throw IllegalStateException("Player $winner already win")
    }
    checkIllegalMove(x, y)

    board[x][y] = nextPlayer

    winner = checkIsWin()
    if (winner != Player.None) {
      return
    }

    nextPlayer = when (nextPlayer) {
      Player.A -> Player.B
      Player.B -> Player.A
      else -> throw IllegalStateException("never been here")
    }
  }

  fun hasWinner() = winner != Player.None

  private fun checkIsWin(): Player {
    var checkWinner: Player = board.checkIfAllLineIsSamePlayer()
    if (checkWinner != Player.None) return checkWinner

    val convertedLine = board.switchColumnToLine()
    checkWinner = convertedLine.checkIfAllLineIsSamePlayer()
    if (checkWinner != Player.None) return checkWinner

    checkWinner = board.checkXLinedIsSamePlayer()
    if (checkWinner != Player.None) return checkWinner

    return Player.None
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