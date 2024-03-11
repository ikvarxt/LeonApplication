package com.example.leonapplication.tdddemo


fun emptyBoard(size: Int = 3) = Array<Array<Player?>>(size) { arrayOfNulls(size) }

fun Array<Array<Player?>>.checkIfAllLineIsSamePlayer(): Player? {
  forEach { line ->
    if (line.all { it is Player.A } && line.size == 3) return Player.A
    else if (line.all { it is Player.B } && line.size == 3) return Player.B
  }
  return null
}

fun Array<Array<Player?>>.switchColumnToLine(): Array<Array<Player?>> {
  val size = this.size
  val convertedLines = emptyBoard(size)
  (0 until size).forEach { i ->
    (0 until size).forEach { l ->
      convertedLines[i][l] = this[l][i]
    }
  }
  return convertedLines
}

fun Array<Array<Player?>>.checkXLinedIsSamePlayer(): Player? {
  val size = this.size

  val winner: Player? = if (size % 2 != 0) {
    // center is null, no body win in X
    this[size / 2][size / 2] ?: return null
  } else null

  var backward = false
  var checkedWinner: Player? = null
  // check both slash direction and backslash direction
  for (i in 0 until 2) {
    for (j in 0 until size) {
      // if (j == size / 2) continue // ignore center player check
      // find next move of X checks
      val k = if (backward) lastIndex - j else j
      if (this[j][k] != winner) {
        checkedWinner = null
        break
      } else checkedWinner = winner
    }
    if (checkedWinner != null) return checkedWinner
    backward = true
  }
  return null
}
