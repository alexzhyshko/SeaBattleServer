import java.io.IOException;
import java.util.Random;

public class Game {

	public long id;
	public String name;
	public User user1;
	public User user2;

	public User first;

	public User turn;

	public Listener l1;
	public Listener l2;

	public Board board;

	public Game(User u1, User u2) {
		this.user1 = u1;
		this.user2 = u2;
		this.board = new Board();
		setTurn();
	}

	private void setTurn() {
		int rand = new Random().nextInt(10);
		if (rand < 5) {
			first = user1;
		} else {
			first = user2;
		}
		turn = first;
	}

	public Game(User u, Listener l, String name) {
		this.user1 = u;
		this.name = name;
		this.l1 = l;
		this.board = new Board();
	}

	public synchronized User getAnotherUser(User me) {
		if (this.user1.equals(me)) {
			if (!this.user2.equals(me)) {
				return this.user2;
			}
		} else if (this.user2.equals(me)) {
			if (!this.user1.equals(me)) {
				return this.user1;
			}
		}
		return null;
	}

	private synchronized Listener getAnotherListener(User me) {
		if (this.user1 != null && this.user2 != null) {
			if (!this.user2.equals(me)) {
				return this.l2;
			}
			if (!this.user1.equals(me)) {
				return this.l1;
			}
		}
		if (this.user1 != null && this.user2 == null) {
			if (this.user1.equals(me)) {
				return null;
			}
		}
		if (this.user2 != null && this.user1 == null) {
			if (this.user2.equals(me)) {
				return null;
			}
		}

		return null;
	}

	public synchronized void messageOpponentAfterQuit(User me, String message) {

		try {
			Listener l = getAnotherListener(me);
			if (l == null) {
				return;
			}
			removeUser(me);
			l.output.write(message);
			l.output.flush();
			l = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void messageOpponent(User me, String message) {

		try {
			Listener l = getAnotherListener(me);
			l.output.write(message);
			l.output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addUser(User u, Listener l) {
		if (user1 == null) {
			this.user1 = u;
			this.l1 = l;
		} else if (user2 == null) {
			this.user2 = u;
			this.l2 = l;
		}
		setTurn();
	}

	public synchronized boolean isJoinable() {
		if (this.user1 == null || this.user2 == null) {
			return true;
		}
		return false;
	}

	public synchronized void removeUser(User u) {
		if (this.user1 != null) {
			if (this.user1.equals(u)) {
				this.user1 = null;
				this.l1 = null;
			}

		}
		if (this.user2 != null) {
			if (this.user2.equals(u)) {
				this.user2 = null;
				this.l2 = null;
			}

		}
	}

	public synchronized String set(int x, int y, User user) {
		if(turn == null) {
			return null;
		}
		if (user == turn) {
			turn = getAnotherUser(user);
			if (user == first) {
				return this.board.set(x, y, "X") ? "cross" : null;
			} else {
				return this.board.set(x, y, "O") ? "circle" : null;
			}
		}
		return null;
	}

	public synchronized boolean setCross(int x, int y) {
		return this.board.set(x, y, "X");
	}

	public synchronized boolean setCircle(int x, int y) {
		return this.board.set(x, y, "O");
	}
	
	public synchronized User checkWinConditions() {
		String winnerChar = board.hasWinner();
		if(winnerChar==null) {
			return null;
		}
		
		if(winnerChar.equals("X")) {
			return first;
		}else {
			return getAnotherUser(first);
		}
	}
	
	public synchronized void end() {
		this.turn = null;
	}
	
	
}
