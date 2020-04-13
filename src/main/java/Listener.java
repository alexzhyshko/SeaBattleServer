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
		System.out.println("Connected from "+this.user.getSocket().getInetAddress());
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
		if(this.game!=null)this.game.removeUser(this.user);
		this.running.set(false);
		Server.removeListener(this.id);
		System.out.println("disconnected from "+this.user.getSocket().getInetAddress());
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
					System.out.println("joindgame: " + gameName);
					boolean joined = false;
					for (Listener l : Server.listeners) {
						if(l.game == null) {
							continue;
						}
						if(l.game.name == null) {
							continue;
						}
						if (l.game.name.strip().equals(gameName.strip())) {
							if(l.game.isJoinable()) {
								l.game.addUser(this.user, this);
								joined = true;
								this.game = l.game;
								System.out.println(this.game.l1==null?"l1 is null":"l1 is not null");
								System.out.println(this.game.l2==null?"l2 is null":"l2 is not null");
								this.game.messageOpponent(this.user, "updateplayers\n");
							}
						}
					}
					
					output.write(joined?"ok\n":"error\n");
					output.flush();
				} else if (command.contains("creategame:")) {
					String gameName = command.substring(11);
					boolean gameExists = false;
					for(Listener l : Server.listeners) {
						if(l.game == null) {
							continue;
						}
						if(l.game.name.strip().equals(gameName.strip())) {
							gameExists = true;
						}
					}
					if(!gameExists) {
						this.game = new Game(this.user, this, gameName);
						//System.out.println("createdgame: " + this.game.name);
					}
					output.write(!gameExists?"ok\n":"error\n");
					output.flush();
				} else if (command.contains("leavegame:")) {
					String gameName = command.substring(10);
					System.out.println("leftgame: " + gameName);
					output.write("leftgame: " + gameName + "\n");
					output.flush();
					for(Listener l : Server.listeners) {
						if(l.game.name.strip().equals(gameName.strip())) {
							this.game.removeUser(this.user);
						}
					}
					output.write("ok\n");
					output.flush();
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
					output.write("disconnected\n");
					output.flush();
					disconnect();
				} else if (command.contains("getgames")) {
					String str = "";
					for (Listener l : Server.listeners) {
						if(l.game == null) {
							continue;
						}
						if (l.game != null) {
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
						System.out.println(username+" logged in from "+this.user.getSocket().getInetAddress());
						output.write("ok\n");
					} else {
						output.write("exists\n");
					}
					output.flush();

					
				}else if(command.contains("getplayers")) {//reponse as user1;user2
					String response = "players:";
					response+=this.game.user1==null?"":this.game.user1.name;
					response+=";";
					response+=this.game.user2==null?"":this.game.user2.name;
					output.write(response+"\n");
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
