import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	static ServerSocket server;
	static List<Listener> listeners = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		server = new ServerSocket(4567);
		while (true) {
			User u = new User(server.accept());
			listeners.add(new Listener(u));
		}
	}

	public static void removeListener(long id) {
		for (int i = 0; i < listeners.size(); i++) {
			Listener l = listeners.get(i);
			if (l.user != null) {
				if (l.user.name != null) {
					if (l.id == id) {
						listeners.remove(i);
						break;
					}
				}
			}
		}
	}

	public static void sendMessage(String username, String message) {
		for (Listener l : listeners) {
			if (l.user != null && l.user.name != null && l.user.name.strip().equals(username.strip())) {
				try {
					l.output.write(message);
					l.output.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
