
public class Game {

	
	public long id;
	public String name;
	private User user1;
	private User user2;
	
	public Game(User u1, User u2) {
		this.user1 = u1;
		this.user2 = u2;
	}
	
	public Game(User u) {
		this.user1 = u;
	}
	
	public User getUser1() {
		return user1;
	}

	public User getUser2() {
		return user2;
	}

	public void addUser(User u) {
		if(user1==null) {
			this.user1 = u;
		}
		else if(user2==null) {
			this.user2 = u;
		}
		
	}
	
	public boolean isJoinable() {
		if(this.user1 !=null &&this.user2!=null) {
			return true;
		}
		return false;
	}
	
	public void removeUser(User u) {
		if(this.user1.equals(u)) {
			this.user1 = null;
		}else if(this.user2.equals(u)) {
			this.user2 = null;
		}
	}
}
