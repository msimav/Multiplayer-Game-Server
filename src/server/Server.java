package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private ServerSocket server;

	private ConcurrentHashMap<String, Player> playerTable;
	private ExecutorService playerPool;

	// TODO:Oyun kuyrukları eklenecek.
	private List<Player> chessQueue;

	//
	private ConcurrentHashMap<String, List<Player>> gameTable;

	private Log log;

	private int port;

	public Server(int port) {
		this.port = port;
		this.playerTable = new ConcurrentHashMap<String, Player>();
		this.playerPool = Executors.newCachedThreadPool();
		initializeLog();
		initializeQueues();
	}

	public void initializeLog() {
		log = new Log();
		log.addPrintStream(System.out);
	}

	public void initializeQueues() {
		this.chessQueue = new LinkedList<Player>();
		gameTable = new ConcurrentHashMap<String, List<Player>>();
		gameTable.put("Chess", chessQueue);
	}

	public void run() {
		Socket socket;
		try {
			server = new ServerSocket(port);
			log.log(String.format("%s - Server running on %s and %d. port.",
					getDate(), server.getInetAddress().getHostAddress(), port));
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

	public class Player implements Runnable {

		private String nick;

		private Scanner input;
		private Formatter output;
		private Socket socket;

		private boolean nickAccepted;

		public Player(Socket socket) {

			try {

				this.socket = socket;
				this.output = new Formatter(new OutputStreamWriter(
						socket.getOutputStream()));
				output.flush();
				this.input = new Scanner(new InputStreamReader(
						socket.getInputStream()));
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
						cmdNICK(params[2]);
					}
				}

				while ((inputMessage = input.nextLine()) != null) {
					log.log(String.format("%s - %s requested %s", getDate(),
							nick, inputMessage));
					handleMessage(nick, inputMessage);
				}
			} catch (IOException e) {
				closeConnection();
				log.log(String.format("%s - %s DISCONNECTED.", getDate(), nick));
			} catch (IllegalStateException e) {
				log.log(String.format("%s - %s DISCONNECTED.", getDate(), nick));
			} catch (NoSuchElementException e) {
				cmdDISCONNECT(nick);
			}

		}

		private void closeConnection() {
			try {
				output.close();
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void cmdNICK(String nick) throws IOException {
			if (hasNick(nick)) {
				cmdERR(output, "Nick already in use");
				return;
			}

			nickAccepted = true;
			this.nick = nick;

			playerTable.put(this.nick, this);
			cmdNICKACCEPTED(output, nick);
			cmdMSG("SERVER",
					String.format("SERVER %s connected to server.", nick));

			log.log(String.format("%s - %s connectted to server.", getDate(),
					nick));
		}

	}

	// METHODS

	private boolean hasNick(String nick) {
		return playerTable.containsKey(nick);
	}

	private void invokeMethod(String command, String from) throws Exception {
		String name = "cmd" + command;
		Class<? extends Server> c = this.getClass();
		Method method = c.getDeclaredMethod(name, String.class);
		method.invoke(this, from);
	}

	private void invokeMethod(String command, String from, String param)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		String name = "cmd" + command;

		Class<? extends Server> s = this.getClass();
		Method method = s.getDeclaredMethod(name, String.class, String.class);
		method.invoke(this, from, param);
	}

	private void handleMessage(String nick, String message) {
		String params[] = message.split(" ", 2);
		try {
			if (params.length != 1) {
				invokeMethod(params[0], nick, params[1]);
			} else {
				invokeMethod(params[0], nick);
			}
		} catch (Exception e) {
			e.printStackTrace();
			cmdERR(nick, "Method invoke error");
		}

	}

	private void cmdJOIN(String from, String game) {
		Player player = playerTable.get(from);
		gameTable.get(game).add(player);

		// TODO burada oyunun yeterli kişi sayısına ulaşıp ulaşmadığı kontrol
		// edilmeli.

	}

	private void cmdGAMES(String nick) {
		Formatter output = getOutput(nick);
		String games = "";

		Enumeration<String> keys = gameTable.keys();

		while (keys.hasMoreElements()) {
			games += (keys.nextElement() + ",");
		}

		output.format("GAMES %s\n", games);
		output.flush();
	}

	private void cmdDISCONNECT(String nick) {
		playerTable.get(nick).closeConnection();
		playerTable.remove(nick);
	}

	private void cmdNICKACCEPTED(Formatter output, String nick) {
		output.format("NICK ACCEPTED %s\n", nick);
		output.flush();
	}

	private void cmdMSG(String from, String param) {
		Iterator<Player> it = playerTable.values().iterator();
		while (it.hasNext()) {
			Formatter formatter = it.next().output;
			formatter.format("MSG %s\n", param);
			formatter.flush();
		}
	}

	private void cmdNAMES(String to) {
		Formatter output = getOutput(to);
		String names = "";

		Enumeration<String> keys = playerTable.keys();

		while (keys.hasMoreElements()) {
			names += (keys.nextElement() + ",");
		}

		output.format("NAMES %s\n", names);
		output.flush();
	}

	private void cmdPRIVMSG(String from, String param) {
		String params[] = param.split(" ", 3);
		String to = params[0];
		if (!hasNick(to)) {
			cmdERR(from, to + " wasn't found");
		}
		String message = params[2];

		// Mesajı Alacak Kisiye
		Formatter output = getOutput(to);
		output.format("PRIVMSG %s %s %s\n", to, from, message);
		output.flush();

		// Mesajı Yollayana
		output = getOutput(from);
		output.format("PRIVMSG %s %s %s\n", to, from, message);
		output.flush();
	}

	private void cmdERR(String to, String message) {
		Formatter output = getOutput(to);
		output.format("ERR %s\n", message);
		output.flush();
	}

	private void cmdERR(Formatter output, String message) {
		output.format("ERR %s\n", message);
		output.flush();
	}

	private Formatter getOutput(String nick) {
		return playerTable.get(nick).output;
	}

	private String getDate() {
		String date = new Date().toString();
		int index = date.indexOf(':');
		date = '[' + date.substring(index - 2, index + 6) + ']';
		return date;
	}

	public static void main(String args[]) {
		Server server = new Server(5556);
		server.run();
	}
}
