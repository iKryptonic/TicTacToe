// @Author: Isaiah Smith
// @Date: 02/09/21

package software;

import java.util.HashMap;
import java.util.Scanner;

/*
 * What do we need?
 * 
 * > class to:
 * - set boxes DONE
 * - get boxes DONE
 * - compute solves in every 
 * 	    column  DONE
 * 		row DONE
 * 		diagonal  DONE
 * 	(all separate methods)
 * - method to find if the board is completely full DONE
 * - method to reset the board DONE
 * - method to print the board DONE
 * 
 * > Driver (Main method) will:
 * - run a scanner loop to handle i/o operations on a tictactoe board class
 * 
 * How do we compute a solve?
 * 	When finding a solution on any row, column, or diagonal: X = -1, O = 1. If any direction adds to the x/y dimension of the board; our game is solved (3, -3)
 * 
 * What is our input?
 *  We'll take inputs using Scanner, each part of the board can be A1, B2, C3 ( Letter = row (x), Number = column (y) )
 *  !!! Any invalid inputs are deferred back to our input function with a bad input message thrown !!!
 * 
 * Things to think about:
 * 	Create a second *COMPUTER* class that is smart-dumb
 * 		- Plays its first move in the middle if open, otherwise pick a random open spot.
 * 		- Pretty much keep placing beside the last box
 *  Allowing for bigger board dimensions (n x n boards)
 *  
 */

public class TicTacToe {

	public enum Box { 

		BAD_PLACEMENT(-2),
		X(-1), 
		EMPTY(0), 
		O(1),
		SOLVED(2),
		RESET_GAME(3);
		
		private int value;
		
	    @Override
	    public String toString() {
	        return Integer.toString(value);
	    }
	    
	    public String asString() {
	    	String rtn;
	    	switch(value) {
	    	case -1:
	    		rtn = "X";
	    		break;
	    	case 0:
	    		rtn = "-";
	    		break;
	    	case 1:
	    		rtn = "O";
	    		break;
	    	default:
	    		rtn = "Something has gone terribly wrong";
	    	}
	    	return rtn;
	    }
	    
		Box(int boxType){
			this.value = boxType;
		}
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	protected static class Board {
		
		// The referee will handle computing tictactoe solves
		private class Referee {
			
			Board currentBoard;
			
			public Referee(Board b) {
				this.currentBoard = b;
			}
			
			protected Board getBoard() {
				return this.currentBoard;
			}
			
			private Box checkSolveColumns() {
				for (int x=0; x <= 2; x++) {
					
					int currentSum = 0;
					
					for(int y=0; y <= 2; y++)
						currentSum+=getBoard().getBox(x, y).value;
						
					if(currentSum == 3)
						return Box.X;
					if(currentSum == -3)
						return Box.O;
				}
				return Box.EMPTY;
			}
			
			protected Box checkSolveRows() {
				for (int y=0; y <= 2; y++) {
					
					int currentSum = 0;
					
					for(int x=0; x <= 2; x++)
						currentSum+=getBoard().getBox(x, y).value;
					if(currentSum == 3)
						return Box.X;
					if(currentSum == -3)
						return Box.O;
				}
				return Box.EMPTY;
			}
			
			private Box checkSolveDiagonals() {
				
				int topLeftToBottomRightSum = 0;
				int topRightToBottomLeftSum = 0;
																		 //  0  1  2 
				topLeftToBottomRightSum+=getBoard().getBox(0, 0).value;// 0 [X][ ][ ]
				topLeftToBottomRightSum+=getBoard().getBox(1, 1).value;// 1 [ ][X][ ]
				topLeftToBottomRightSum+=getBoard().getBox(2, 2).value;// 2 [ ][ ][X]
																		 //  0  1  2 
				topRightToBottomLeftSum+=getBoard().getBox(2, 0).value;// 0 [ ][ ][X]
				topRightToBottomLeftSum+=getBoard().getBox(1, 1).value;// 1 [ ][X][ ]
				topRightToBottomLeftSum+=getBoard().getBox(0, 2).value;// 2 [X][ ][ ]

				if(topLeftToBottomRightSum == 3)
					return Box.X;
				if(topLeftToBottomRightSum == -3)
					return Box.O;
				if(topRightToBottomLeftSum == 3)
					return Box.X;
				if(topRightToBottomLeftSum == -3)
					return Box.O;
				
				return Box.EMPTY;
			}
			
			public Box checkSolve() {
				
				if(isFull())
					return Box.SOLVED;
				
				// Observe this is "checked" not "check" > NAMING CONVENTIONS ROCK!!!
				Box checkedSolveRows = checkSolveRows();
				Box checkedSolveColumns = checkSolveColumns();
				Box checkedSolveDiagonals = checkSolveDiagonals();
				
				// return whatever guy won
				if(checkedSolveRows != Box.EMPTY) {
					return checkedSolveRows;}
				if(checkedSolveColumns != Box.EMPTY) {
					return checkedSolveColumns;}
				if(checkedSolveDiagonals != Box.EMPTY) {
					return checkedSolveDiagonals;}
				
				// fall back to an empty return
			return Box.EMPTY;
			}
			
		}
		
		Referee referee;
		
		protected Box boardInternal[][];
		
		public Board() {
			this.referee = new Referee(this);
			this.boardInternal = new Box[3][3];
			for(int x=0; x<=2; x++) {
				for(int y=0; y<=2; y++) {
					this.boardInternal[x][y] = Box.EMPTY; // initialize every slot with an empty box
				}
			}
			/* Ideally, this is our data structure for the boxes
			 * 
			 * [EMPTY][EMPTY][EMTPY]
			 * [EMPTY][EMPTY][EMTPY]
			 * [EMPTY][EMPTY][EMTPY]
			 */
		}
		
		public Box getBox(int x, int y) {
			if(x > 2 || y > 2 || x < 0 || y < 0)
				return Box.BAD_PLACEMENT;
			
			return this.boardInternal[x][y];
		}
		
		public boolean isFull() {
			// neat trick i picked up from lua to check a boolean in a return method
			for(int x=0; x<=2; x++) {
				for(int y=0; y<=2; y++) {
					if(getBox(x, y)==Box.EMPTY)
						return false;
				}
			}
			return true;
		}
		
		public void resetGame() {
			// Reset the board c:
			this.referee = new Referee(this);
			this.boardInternal = new Box[3][3];
			for(int x=0; x<=2; x++) {
				for(int y=0; y<=2; y++) {
					this.boardInternal[x][y] = Box.EMPTY; // initialize every slot with an empty box
				}
			}
			System.out.println("*** Game reset ***");
			System.out.println("*** Welcome to TicTacToe! ***");
			System.out.println("*** Type RESET to restart! ***");
			System.out.println("*** Type QUIT to exit! ***");
		}
		
		public Box setBox(int x, int y, Box player) {
			
			if(this.referee.checkSolve() == Box.SOLVED || player==Box.RESET_GAME) { resetGame(); return Box.RESET_GAME;} // board is already solved
			
			Box currentBox = getBox(x, y); // get the current value of the box
			
			Box rtn = null;
			
			switch(currentBox) {
				case EMPTY: // if the box is empty, we fill it with the player requesting the area
					this.boardInternal[x][y] = player; // set the field
					rtn = this.referee.checkSolve();
					break; // break out of our switch
				case BAD_PLACEMENT:
					System.out.println("That is a invalid placement option!");
					rtn = Box.BAD_PLACEMENT;
					break;
				default: // if the box is not EMPTY, we need to throw a invalid placement exception to notify the caller
					System.out.println("That box is filled option!");
					rtn = Box.BAD_PLACEMENT;
			}
		System.out.println(this.toString());
		return rtn;
		}
		
		public String toString() {
			StringBuilder boxResult = new StringBuilder(); // save the memories! (bad save the trees pun)
			
			boxResult.append(String.format("\n   1  2  3\nA [%s][%s][%s]\nB [%s][%s][%s]\nC [%s][%s][%s]\n",
					this.boardInternal[0][0].asString(),
					this.boardInternal[0][1].asString(),
					this.boardInternal[0][2].asString(),
					this.boardInternal[1][0].asString(),
					this.boardInternal[1][1].asString(),
					this.boardInternal[1][2].asString(),
					this.boardInternal[2][0].asString(),
					this.boardInternal[2][1].asString(),
					this.boardInternal[2][2].asString()
					));
			
			return boxResult.toString();
		}
	}
	
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		Board ourBoard = new Board();
		
		HashMap<String, Box> Players = new HashMap<String, Box>();
		Players.put("Player1", Box.X);
		Players.put("Player2", Box.O);
		
		String currentPlayer = "Player1";
		
		System.out.println("*** Welcome to TicTacToe! ***");
		System.out.println("*** Type RESET to restart! ***");
		System.out.println("*** Type QUIT to exit! ***");
		System.out.println(ourBoard.toString());
		do {
			System.out.printf("Your move, %s: ",currentPlayer);
			String desiredInput = keyboard.next();
			
			Box inputResult = null;
			
			if(desiredInput.toLowerCase().equals("quit")) // quit the program immediately
				break;
			if(desiredInput.toLowerCase().equals("reset")) // reset the game
				inputResult = ourBoard.setBox(0, 0, Box.RESET_GAME);
			else if(desiredInput != null) {
				// we got an input let's see where we're going with this
				if(desiredInput.length() != 2) {
					// wrong answer buddy
					System.out.printf("Hey, %s! Please enter your format in the format of \"LetterNumber\". ex. A2%n%s is in the wrong format!%n",
							currentPlayer,
							desiredInput);
					continue;
				} else {
					// this is probably the correct format... let's check
					char columnPicked = desiredInput.toLowerCase().charAt(0);
					String rowPicked = desiredInput.split("")[1]; // get the 2 characters input
					
					if(isInteger(rowPicked)) {
						int decidedRow = Integer.parseInt(rowPicked);
						decidedRow--;
						switch(columnPicked) {
						case 'a':
							inputResult = ourBoard.setBox(0, decidedRow, Players.get(currentPlayer));
							break;
						case 'b':
							inputResult = ourBoard.setBox(1, decidedRow, Players.get(currentPlayer));
							break;
						case 'c':
							inputResult = ourBoard.setBox(2, decidedRow, Players.get(currentPlayer));
							break;
						default:
							System.out.println("Could not decipher your column (A,B,C allowed.)");
							continue;
						}
					} else {
						System.out.println("Could not decipher your row (Only numbers allowed)");
						continue;
					}
					
				}
			}
				
			// do a reset check here
			switch(inputResult) {
				case X:
					System.out.println("Player 2 wins!"); // player2 win
					currentPlayer = "Player1";
					ourBoard.resetGame();
					break;
				case O:
					System.out.println("Player 1 wins!"); // player1 win
					currentPlayer = "Player1";
					ourBoard.resetGame();
					break;
				case RESET_GAME:
					System.out.printf("%n%s requested a reset for the previous game. Bad sport!%n", currentPlayer);
					currentPlayer = "Player1";
					break;
				case BAD_PLACEMENT:
					// nothing to really handle here but we should catch every possible outcome
					break;
				case SOLVED: // yeah misleading title but this is the literal last line i wrote. this doesn't mean solved, it's a draw
					System.out.println("Draw game! Better luck next time!");
					currentPlayer = "Player1";
					ourBoard.resetGame();
					break;
				case EMPTY:
					// give each player a turn
					if(currentPlayer.equals("Player1"))
						currentPlayer = "Player2";
					else
						currentPlayer = "Player1";
					break;
				default:
					System.out.println("Something went wrong!");
					System.out.println("RESULT: "+inputResult);
			}
			
		} while(true);
		
		keyboard.close();
	}

}
