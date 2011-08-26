package server;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private ServerSocket server;
	private ConcurrentHashMap<String, Player> playerTable;
	private ExecutorService playerPool;

	private Log log;

	private int port;

	public Server(int port) {
		this.port = port;
		this.playerTable = new ConcurrentHashMap<String, Player>();
		this.playerPool = Executors.newCachedThreadPool();
		log = new Log();
		log.addPrintStream(System.out);
	}

	public void run() {
		Socket socket;
		try {
			server = new ServerSocket(port);
			log.log(String.format("Server running on %s and %d. port.", server
					.getInetAddress().getHostAddress(), port));
			while (true) {
				socket = server.accept();
				Player player = new Player(socket);
				playerPool.execute(player);

			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	private class Player implements Runnable {

		private String nick;

		private Scanner input;
		private Formatter output;
		private Socket socket;

		private boolean nickAccepted;

		public Player(Socket socket) {

			try {

				this.socket = socket;
				this.output = output;
				output.flush();
				this.input = new Scanner(socket.getInputStream());
				nickAccepted = false;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			String inputMessage;
			String params[];
			try {
				while (!nickAccepted) {
					inputMessage = input.nextLine();
					params = inputMessage.split(" ");
					if (params.length == 3 && params[0].equals("NICK")
							&& params[1].equals("SET")) {
						cmdNICKSET(params[2]);
					}
				}

				while ((inputMessage = input.nextLine()) != null) {
					handleMessage(nick, inputMessage);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				cmdDISCONNECT(nick);
			}

		}

		private void cmdNICKSET(String nick) throws IOException {
			if (hasNick(nick)) {
				cmdERR(output, "Nick already in use");
				return;
			}

			nickAccepted = true;
			this.nick = nick;

			playerTable.put(this.nick, this);
			cmdNICKACCEPTED(output, nick);
			cmdMSG("SERVER", String.format("%s connected to server.", nick));

			log.log(String.format("%s connectted to server.", nick));
		}

	}

	// METHODS

	private boolean hasNick(String nick) {
		return playerTable.containsKey(nick);
	}

	private void invokeMethod(String command, String from) throws Exception {
		String name = "cmd" + command;
		Method method = this.getClass().getMethod(name, String.class);
		method.invoke(this, from);
	}

	private void invokeMethod(String command, String from, String param)
			throws Exception {
		String name = "cmd" + command;
		Method method = this.getClass().getMethod(name, String.class,
				String.class);
		method.invoke(this, from, param);
	}

	private void handleMessage(String nick, String message) {
		String params[] = message.split(" ", 1);
		try {
			if (params.length != 1) {
				invokeMethod(params[0], nick, params[1]);
			} else {
				invokeMethod(params[0], nick);
			}
		} catch (Exception e) {
			cmdERR(nick, "Method invoke error");
		}

	}

	private void cmdDISCONNECT(String player) {
		playerTable.remove(player);
	}

	private void cmdNICKACCEPTED(Formatter output, String nick) {
		output.format("NICK ACCEPTED %s", nick);
		output.flush();
	}

	private void cmdMSG(String from, String param) {

		Iterator<Player> it = playerTable.values().iterator();
		while (it.hasNext()) {
			Formatter formatter = it.next().output;
			formatter.format("MSG %s %s", from, param);
			formatter.flush();
		}
	}

	private void cmdNAMES(String to) {
		Formatter output = getOutput(to);
		String names = null;

		Enumeration<String> keys = playerTable.keys();

		while (keys.hasMoreElements()) {
			names += keys.nextElement() + ",";
		}

		output.format("NAMES %s", names);
		output.flush();
	}

	private void cmdPRIVMSG(String from, String param) {
		String params[] = param.split(" ", 3);
		String to = params[0];
		String message = params[2];
		Formatter output = getOutput(to);
		output.format("PRIVMSG %s %s %s", to, from, message);
		output.flush();
	}

	private void cmdERR(String to, String message) {
		Formatter output = getOutput(to);
		output.format("ERR %s", message);
		output.flush();
	}

	private void cmdERR(Formatter output, String message) {
		output.format("ERR %s", message);
		output.flush();
	}

	private Formatter getOutput(String nick) {
		return playerTable.get(nick).output;
	}

}
