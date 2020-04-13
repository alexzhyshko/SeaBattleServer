import java.io.IOException;
import java.net.Socket;

public class User {

	public long id;
	public String name;
	private	Socket socket;
	
	public User(Socket client) {
		this.id = this.hashCode();
		this.socket = client;
	}
	
	
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public boolean disconnect() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
