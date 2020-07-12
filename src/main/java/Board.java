import java.util.Arrays;

public class Board {

	public long id;
	
	private String[][] board = new String[3][3];
	
	public Board() {
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				board[i][j] = "";
			}
		}
	}
	
	public boolean set(int x, int y, String symbol) {
		if(!Arrays.asList("X", "O").contains(symbol)) {
			return false;
		}
		if(!board[y][x].isBlank()) {
			return false;
		}
		board[y][x] = symbol;
		return true;
	}
	
	
	public String hasWinner() {
		String result = null;
		if(checkRows()!=null || checkColumns()!=null || checkDiagonals()!=null) {
			result = checkRows()!=null?checkRows():checkColumns()!=null?checkColumns():checkDiagonals();
		}
		return result;
	}
	
	
	private String checkRows() {

		for (int i = 0; i < board[0].length; i++) {
			if(!board[i][0].isBlank()&&!board[i][1].isBlank()&&!board[i][2].isBlank()) {
				if(board[i][0].trim().equals(board[i][1].trim())&&board[i][1].trim().equals(board[i][2].trim())) {
					return board[i][0];
				}
			}
		}
		return null;

	}

	private String checkColumns() {
		for (int j = 0; j < board.length; j++) {
			if(!board[0][j].isBlank()&&!board[1][j].isBlank()&&!board[2][j].isBlank()) {
				if(board[0][j].trim().equals(board[1][j].trim())&&board[1][j].trim().equals(board[2][j].trim())) {
					return board[0][j];
				}
			}

		}

		return null;

	}

	private String checkDiagonals() {

		if(!board[0][0].isBlank()&&!board[1][1].isBlank()&&!board[2][2].isBlank()) {
			if(board[0][0].trim().equals(board[1][1].trim())&&board[1][1].trim().equals(board[2][2].trim())) {
				return board[0][0];
			}
		}
		if(!board[2][0].isBlank()&&!board[1][1].isBlank()&&!board[0][2].isBlank()) {
			if(board[2][0].trim().equals(board[1][1].trim())&&board[1][1].trim().equals(board[0][2].trim())) {
				return board[2][0];
			}
		}

		return null;
	}
	
	
}
