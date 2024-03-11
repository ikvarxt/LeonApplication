package com.example.leonapplication.tdddemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TicTocToeTest {

  private lateinit var board: TicTocToe

  @Before
  fun setup() {
    board = TicTocToe()
  }

  @Test
  fun whenXOutsideBoardThanException() {
    assertThrows(RuntimeException::class.java) {
      board.play(5, 2)
    }
  }

  @Test
  fun whenYOutsideBoardThanException() {
    assertThrows(RuntimeException::class.java) {
      board.play(2, 5)
    }
  }

  @Test
  fun whenMoveAlreadyBeenTakenThanIllegalException() {
    board.play(2, 2)
    assertThrows(IllegalArgumentException::class.java) {
      board.play(2, 2)
    }
  }

  @Test
  fun whenFirstMoveIsA() {
    assertEquals(Player.A, board.nextPlayer)
  }

  @Test
  fun whenFirstMoveIsAThanNextIsB() {
    assertEquals(Player.A, board.nextPlayer)
    board.play(0, 0)
    assertEquals(Player.B, board.nextPlayer)
  }

  @Test
  fun whenLineAllByAThanAWin() {
    board.play(0, 0) // A
    board.play(1, 1) // B
    board.play(0, 1) // A
    board.play(1, 2) // B
    board.play(0, 2) // A

    assertEquals(Player.A, board.winner)
  }

  @Test
  fun whenColumnAllByBThanBWin() {
    board.apply {
      play(1, 2) // A
      play(1, 1) // B
      play(0, 0) // A
      play(0, 1) // B
      play(2, 2) // A
      play(2, 1) // B
    }
    assertEquals(Player.B, board.winner)
  }

  @Test
  fun whenAAlreadyWinThanException() {
    board.play(0, 0) // A
    board.play(1, 1) // B
    board.play(0, 1) // A
    board.play(1, 2) // B
    board.play(0, 2) // A

    assertEquals(Player.A, board.winner)
    assertThrows(IllegalStateException::class.java) {
      board.play(2, 2)
    }
  }

  @Test
  fun whenAHasXLinedThanAWin() {
    board.apply {
      play(1, 1) // A
      play(0, 1) // B
      play(0, 0) // A
      play(1, 0) // B
      play(2, 2) // A
    }
    val expected = """
      | A | B |   |
      -------------
      | B | A |   |
      -------------
      |   |   | A |
    """.trimIndent()
    assertEquals(expected, board.display())
    assertEquals(Player.A, board.winner)
  }

  @Test
  fun whenABackwardXLinedThanAWin() {
    board.apply {
      play(1, 1) // A
      play(0, 1) // B
      play(2, 0) // A
      play(1, 0) // B
      play(0, 2) // A
    }
    val expected = """
      |   | B | A |
      -------------
      | B | A |   |
      -------------
      | A |   |   |
    """.trimIndent()
    assertEquals(expected, board.display())
    assertEquals(Player.A, board.winner)
  }

  @Test
  fun whenGameHasWinnerThanHasWinner() {
    board.play(0, 0) // A
    assertFalse(board.hasWinner)
    board.play(1, 1) // B
    board.play(0, 1) // A
    board.play(1, 2) // B
    board.play(0, 2) // A
    assertTrue(board.hasWinner)
  }

  @Test
  fun whenStartGameThanPrintEmptyBoard() {
    val display = board.display()
    val expectedDisplay = """
      |   |   |   |
      -------------
      |   |   |   |
      -------------
      |   |   |   |
    """.trimIndent()
    assertEquals(expectedDisplay, display)
  }

  @Test
  fun whenAMoveThanDisplayAMovedBoard() {
    board.play(1, 1)
    val expected = """
      |   |   |   |
      -------------
      |   | A |   |
      -------------
      |   |   |   |
    """.trimIndent()
    assertEquals(expected, board.display())
  }

  @Test
  fun whenBWinThanBWinDisplay() {
    board.apply {
      play(1, 2) // A
      play(1, 1) // B
      play(0, 0) // A
      play(0, 1) // B
      play(2, 2) // A
      play(2, 1) // B
    }
    val expected = """
      | A | B |   |
      -------------
      |   | B | A |
      -------------
      |   | B | A |
    """.trimIndent()
    assertEquals(expected, board.display())
  }
}