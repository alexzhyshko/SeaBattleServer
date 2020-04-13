import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Listener extends Thread {

	long id;
	User user;
	Game game;
	
	BufferedReader input;
	BufferedWriter output;

	private AtomicBoolean running = new AtomicBoolean(true);

	private Socket socket;

	
	
	
	public Listener(User u) {
		this.id = this.getId();
		this.user = u;
		start();
	}

	public Listener() {
		this.id = this.getId();
		start();
	}

	public void setUserName(String name) {
		this.user.name = name;
	}

	public void setGame(Game g) {
		this.game = g;
	}

	public void disconnect() {
		this.user.disconnect();
		this.running.set(false);
	}

	@Override
	public void run() {
		this.socket = user.getSocket();
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			}catch(IOException e) {
				e.printStackTrace();
			}
		
		while (true) {
			try {
				String command = input.readLine();
				if (command.contains("joingame:")) {
					String gameName = command.substring(9);
					System.out.println("joindgame: " + gameName);
					output.write("joindgame: " + gameName + "\n");
					output.flush();
					// TODO find game by name and store here
				} else if (command.contains("creategame:")) {
					String gameName = command.substring(11);
					System.out.println("createdgame: " + gameName);
					output.write("createdgame: " + gameName + "\n");
					output.flush();
					// TODO create new game and store here
					// TODO if game exists - answer with prompt to change name and try again
				} else if (command.contains("leavegame:")) {
					String gameName = command.substring(10);
					System.out.println("leftgame: " + gameName);
					output.write("leftgame: " + gameName + "\n");
					output.flush();
					// TODO find a game by name and leave it
				}
				// like setship:1,1;3,3
				else if (command.contains("setship:")) {
					String coord = command.substring(8);
					String startcoord = coord.split(";")[0];
					String endcoord = coord.split(";")[1];
					int x1 = Integer.parseInt(startcoord.split(",")[0]);
					int y1 = Integer.parseInt(startcoord.split(",")[1]);
					int x2 = Integer.parseInt(endcoord.split(",")[0]);
					int y2 = Integer.parseInt(endcoord.split(",")[1]);
					System.out.println("setship:{" + x1 + ";" + y1 + "};{" + x2 + ";" + y2 + "}");
					output.write("setship:{" + x1 + ";" + y1 + "};{" + x2 + ";" + y2 + "}\n");
					output.flush();
					// TODO set a ship on these coordinates
				}
				// like shoot:1,1
				else if (command.contains("shoot:")) {
					String coord = command.substring(6);
					int x = Integer.parseInt(coord.split(",")[0]);
					int y = Integer.parseInt(coord.split(",")[1]);
					System.out.println("shotat: {" + x + "," + y + "}");
					output.write("shotat: {" + x + "," + y + "}\n");
					output.flush();
					// TODO answer with sector status: NO, HIT, DEAD
				} else if (command.contains("disconnect")) {
					System.out.println("disconnected");
					output.write("disconnected\n");
					output.flush();
					disconnect();
				} else if (command.contains("getgames")) {
					String str = "";
					for (Listener l : Server.listeners) {
						if (l.game != null) {
							str += game.name + ",";
						}
					}
					str = str.length() > 1 ? str.substring(0, str.length() - 1) : str;
					output.write(str + "\n");
					output.flush();
				} else if (command.contains("login:")) {

					boolean exists = false;
					String username = command.substring(6);
					for (Listener l : Server.listeners) {
						if (l.user.name != null) {
							if (l.user.name.trim().equals(username.trim())) {
								exists = true;
							}
						}
					}
					if(!exists) {
						setUserName(username);
						output.write("ok\n");
					}else {
						output.write("exists\n");
					}
					output.flush();
					
				}

			} catch (SocketException e) {
				disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
