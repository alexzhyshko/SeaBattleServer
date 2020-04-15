import java.io.IOException;

public class Game {

	public long id;
	public String name;
	public User user1;
	public User user2;

	public Listener l1;
	public Listener l2;

	public Board board;

	public Game(User u1, User u2) {
		this.user1 = u1;
		this.user2 = u2;
		this.board = new Board();
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
		if(this.user1!=null && this.user2!=null) {
				if (!this.user2.equals(me)) {
					return this.l2;
				}
				if (!this.user1.equals(me)) {
					return this.l1;
				}
		}
		if(this.user1!=null && this.user2==null) {
			if(this.user1.equals(me)) {
				return null;
			}
		}
		if(this.user2!=null && this.user1==null) {
			if(this.user2.equals(me)) {
				return null;
			}
		}
		
		return null;
	}

	public synchronized void messageOpponentAfterQuit(User me, String message) {

		try {
			Listener l = getAnotherListener(me);
			if(l==null) {
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
}
