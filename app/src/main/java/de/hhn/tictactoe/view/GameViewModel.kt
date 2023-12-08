package de.hhn.tictactoe.view

import android.util.Log
import androidx.lifecycle.ViewModel
import de.hhn.tictactoe.model.Field
import de.hhn.tictactoe.model.GameModel
import de.hhn.tictactoe.model.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
/**
 * ViewModel class responsible for managing the game state and logic.
 *
 * The class includes functionality for resetting the game, selecting a game field,
 * and checking for the end of the game by analyzing rows, columns, and diagonals.
 */
class GameViewModel(
    private var currentPlayer: Status = Status.PlayerX,
   private var winningPlayer: Status = Status.Empty,
    private var isGameEnding: Boolean = false
): ViewModel() {

    /**
     * Represents the game field as a 2D array of Fields.
     */
    private val _gameViewModelField = MutableStateFlow(
       List(3) {col ->
                    List(3){row ->
                        Field( indexColumn = col, indexRow = row)

                    }
        }
    )

    val gameViewModelField: StateFlow<List<List<Field>>> = _gameViewModelField.asStateFlow()


/**
 * Resetting the Game
 * Code requirement
  * In HomeScreen.kt TODO beachten
   *In MainActivity.kt TODO beachten
 *
 **/
fun resetGame() {
    val resetGameField = List(3) { col ->
        List(3) { row ->
            Field(indexColumn = col, indexRow = row, status = Status.Empty)
        }
    }
    _gameViewModelField.value = resetGameField
}



    /**
     * Code requirement
     * Hier besteht die Aufgabe darin, die Aktiverung der Boxen zu implementieren.
     * In HomeScreen.kt TODO beachten
     */
    fun selectField(field: Field) {

        val updatedField  = _gameViewModelField.value.toMutableList().map { it.toMutableList()}
        updatedField[field.indexColumn][field.indexRow] = Field(
            indexColumn = field.indexColumn,
            indexRow = field.indexRow,
            status = currentPlayer
        )
        // Update the game Viewmodel
        _gameViewModelField.value = updatedField
        //Change the currentPlayer for the next player
        currentPlayer = currentPlayer.next()

    }

    // Hier werden die Spalten, Reihen und Diagonalen überprüft und das Game beendet, falls ein Spieler gewonnen hat.
    fun checkEndingGame(){

        val grid = _gameViewModelField.value

         fun checkRows(): Status? {
            for (row in grid) {
                if (row.all { it.status == Status.PlayerX }) {
                    return Status.PlayerX
                } else if (row.all { it.status == Status.PlayerO }) {
                    return Status.PlayerO
                }
            }
            return null
        }

        fun checkColumns(): Status? {
            for (col in grid[0].indices) {
                if (grid.all { it[col].status == Status.PlayerX }) {
                    return Status.PlayerX
                } else if (grid.all { it[col].status == Status.PlayerO }) {
                    return Status.PlayerO
                }
            }
            return null
        }

         fun checkDiagonals(): Status? {
            if (grid[0][0].status == grid[1][1].status && grid[1][1].status == grid[2][2].status) {
                return grid[0][0].status
            } else if (grid[0][2].status == grid[1][1].status && grid[1][1].status == grid[2][0].status) {
                return grid[0][2].status
            }
            return null
        }

        val winner = checkRows() ?: checkColumns() ?: checkDiagonals()

        if (winner != null) {
            isGameEnding = true

            // Update the status of each Field in the winning line
            when (winner) {
                Status.PlayerX -> updateFieldStatus(grid, checkWinningRow(grid, Status.PlayerX), Status.PlayerX)
                Status.PlayerO -> updateFieldStatus(grid, checkWinningRow(grid, Status.PlayerO), Status.PlayerO)
                else -> {}
            }
        }
    }
        // update the winnerstate


 private fun updateFieldStatus(grid: List<List<Field>>, winningLine: List<Pair<Int, Int>>, winner: Status) {
    val updatedField = _gameViewModelField.value.toMutableList().map { it.toMutableList() }
    for ((col, row) in winningLine) {
        updatedField[col][row].status = winner
    }
    _gameViewModelField.value = updatedField
}

 private fun checkWinningRow(grid: List<List<Field>>, player: Status): List<Pair<Int, Int>> {
     for (row in grid.indices) {
         if (grid[row].all { it.status == player }) {
             // Return the indices of the winning row
             return List(grid[row].size) { col -> Pair(row, col) }
         }
     }
     return emptyList()
}
}
