import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	static ServerSocket server;
	static List<Listener> listeners = new ArrayList<>();
	public static void main(String[] args) throws IOException {

		server = new ServerSocket(9000);
		while(true) {
			User u = new User(server.accept());
			listeners.add(new Listener(u));
		}
	}
	
	
	

}
