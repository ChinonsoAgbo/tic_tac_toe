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

    // StateFlow for the Gamemodel
    private val _gameModel = MutableStateFlow(GameModel())
    val gameModel: StateFlow<GameModel> = _gameModel.asStateFlow()

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
  *TODO In HomeScreen.kt  beachten
   * TODO In MainActivity.ktbeachten
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
     *  Aufgabe: Hier besteht die Aufgabe darin, die Aktiverung der Boxen zu implementieren.
     * TODO In HomeScreen.kt beachten
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
    /**
     * Checks the game state to determine if it has reached a conclusion, either due to a player winning .
     * Updates the game model state accordingly.
     * !!! The state of Draw is not determined
     * This method examines the rows, columns, and diagonals of the game field to identify a winning player.
     * If a winner is found, the winning player is updated, and the game is marked as ending.
     * The updated game state is then reflected in the [_gameModel] state flow.
     * TODO Hier werden die Spalten, Reihen und Diagonalen überprüft und das Game beendet, falls ein Spieler gewonnen hat.
     */

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

        Log.e("CheckRow",checkRows().toString())
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
            // Update the winningPlayer property and set isGameEnding to true
            winningPlayer = winner
            isGameEnding = true
            Log.e("winner",winningPlayer.toString())

        }
          // Update the _gameModel state
        _gameModel.value = GameModel(currentPlayer, winningPlayer, isGameEnding)

    }


}