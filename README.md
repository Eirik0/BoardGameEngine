# BoardGameEngine


This program is an experiment in Artificial Intelligence, more specifically board game analysis.
The user is able to play against the computer in a variety of board games, or watch matches against
different AIs.


To compile and run the program on windows requires the Java Development Kit 8, and that Java's 'bin'
directory be referenced in the systems environment variables (see Oracle's website for instructions).
Clone the repository and then run the batch script 'compileAndRun.bat' located in the root directory.
The first time this is run it will create and run an executable jar. Subsequent runs will check for 
the existence of 'BoardGameEngine.jar' and then run that without recompiling. If compilation fails, it
will be necessary to remove this jar before rerunning the batch script.

Alternatively, one can download the Eclipse IDE, clone this project, and run it through the IDE.


Currently there exist two main varieties of AI for determining the best moves in a given position of a
game: a multi-threaded minmax/alphabeta style strategy, and a single-threaded monte carlo strategy.
After selecting a game, the user may select players from a drop down menu at the top of the screen.
If a computer player is selected, it can be configured by pressing the 'C' button which appears next
to the menu. Games can then be started/stopped by pressing the 'Play'/'Pause' button, or reset by
pressing 'New Game'. It is also possible to analyze a given position by using the analysis pane on
the right side of the screen, and navigate the history of the game by selecting a move on the move
history pane on the left side of the screen.

In addition to Chess, Tic Tac Toe, Ultimate Tic Tac Toe, and Gomoku, this program also features two
computer-only simulations: a Sudoku generator which starts with a blank board, and, using the computer
strategies, generates solved Sudoku puzzles; and a visual representation of the game-tree searching
algorithms, entitled 'Fork Join Example'.


Please enjoy this program and thank you for checking it out!
