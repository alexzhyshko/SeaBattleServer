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
		if (this.game != null)
			this.game.removeUser(this.user);
		this.running.set(false);
		Server.removeListener(this.id);
	}

	@Override
	public void run() {
		this.socket = user.getSocket();
		try {
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (running.get()) {
			try {
				String command = input.readLine();
				if (command.contains("joingame:")) {
					String gameName = command.substring(9);
					boolean joined = false;
					for (Listener l : Server.listeners) {
						if (l.game == null) {
							continue;
						}
						if (l.game.name == null) {
							continue;
						}
						if (l.game.name.strip().equals(gameName.strip())) {
							if (l.game.isJoinable()) {
								l.game.addUser(this.user, this);
								joined = true;
								this.game = l.game;
								this.game.messageOpponent(this.user, "updateplayers\n");
							}
						}
					}

					output.write(joined ? "ok\n" : "error\n");
					output.flush();
				} else if (command.contains("creategame:")) {
					String gameName = command.substring(11);
					boolean gameExists = false;
					for (Listener l : Server.listeners) {
						if (l.game == null) {
							continue;
						}
						if (l.game.name.strip().equals(gameName.strip())) {
							gameExists = true;
						}
					}
					if (!gameExists) {
						this.game = new Game(this.user, this, gameName);
					}
					output.write(!gameExists ? "ok\n" : "error\n");
					output.flush();
				} else if (command.contains("leavegame")) {
					output.write("leaveok\n");
					output.flush();
					this.game.messageOpponentAfterQuit(this.user, "updateplayers\n");
					this.game = null;
				}
				// like setcross:1,1
				else if (command.contains("set:")) {

					String coord = command.substring(4);
					String result = null;

					int x = 0;
					int y = 0;
					try {
						x = Integer.parseInt(coord.split(",")[0]);
						y = Integer.parseInt(coord.split(",")[1]);
						result = game.set(x, y, this.user);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					User hasWinner = game.checkWinConditions();

					if (result !=null && hasWinner != null) {
						if(hasWinner==this.user) {
							game.messageOpponent(this.user, "loser:"+x+","+y+","+result+"\n");
							output.write("winner:"+x+","+y+","+result+"\n");
							output.flush();
						}else {
							game.messageOpponent(this.user, "winner:"+x+","+y+","+result+"\n");
							output.write("loser:"+x+","+y+","+result+"\n");
							output.flush();
						}
						game.end();
					}

					else if (result != null) {
						game.messageOpponent(this.user, result + ":" + x + "," + y + "\n");
						output.write(result != null ? result + ":" + x + "," + y + "\n" : "error\n");
						output.flush();
					}

				} else if (command.contains("disconnect")) {
					if (this.game == null) {
						continue;
					}
					if (this.game != null) {
						this.game.messageOpponentAfterQuit(this.user, "updateplayers\n");
					}
					socket.close();
				} else if (command.contains("getgames")) {
					String str = "";
					for (Listener l : Server.listeners) {
						if (l.game == null) {
							continue;
						}
						if (l.game != null && l.game.isJoinable()) {
							str += l.game.name + ",";
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
					if (!exists) {
						setUserName(username);
						output.write("ok\n");
					} else {
						output.write("exists\n");
					}
					output.flush();

				} else if (command.contains("getplayers")) {// reponse as user1;user2
					String response = "players:";
					response += this.game.user1 == null ? " " : this.game.user1.name;
					response += ";";
					response += this.game.user2 == null ? " " : this.game.user2.name;
					output.write(response + "\n");
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
