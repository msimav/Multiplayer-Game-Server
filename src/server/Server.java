package server;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.omg.CORBA.portable.OutputStream;



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
			log.log(String.format("Server running on %s and %d.port.", 
					server.getInetAddress().getHostAddress(), port));
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
			String message;
			String params[];

			while (!nickAccepted) {
				message = input.nextLine();
				params = message.split(" ");
				if (params.length == 2 && params[0].equals("SETNICK")){
					if(hasNick(params[1])){
						cmdERR("Nick already in use");
					}else{
						cmdSETNICK(params[1]);
					}
				}
			}

			while ((message = input.nextLine()) != null) {
				handleMessage(message);
			}

		}
		
		private boolean hasNick(String nick){
			return playerTable.containsKey(nick);
		}
		
		private void cmdSETNICK(String nick) {
			nickAccepted = true;
			this.nick = nick;
			playerTable.put(this.nick, this);
			cmdMESSAGE("SERVER", String.format("%s connected to server.", nick));
			log.log(String.format("%s connectted to server.", nick));
		}
		
		private void invokeMethod(String command, String param) throws Exception{
			String name = "cmd" + command;
			Method method = getClass().getMethod(name, String.class );
			method.invoke(getClass(), param);
		}
		
		private void handleMessage(String message) {
			String params[] = message.split(" ", 1);
			try {
				invokeMethod(params[0], params[1]);
			} catch (Exception e) {
				cmdERR("Method invoke error");
			}
			
		}
		
		private void cmdMESSAGE(String sender, String message) {
			Iterator<Player> it = playerTable.values().iterator();
			while (it.hasNext()) {
				Formatter formatter = it.next().output;
				formatter.format("MESSAGE %s", message);
				formatter.flush();
			}
		}
		
		private void cmdERR(String message){
			output.format("ERR %s", message);
			output.flush();
		}
	}
	
	
	
	

	

}
