package client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.HashMap;

import client.gui.SwingWaitingRoom;

public class Client {
	private GraphicalUserInterface gui;
	private String nick;
	private Formatter outputStream;
	private BufferedReader inputStream;
	private Socket sock;
	private String[] onlines;
	private HashMap<String, Integer> games;

	public Client(String server, int port, String aNick,
			GraphicalUserInterface aGui) {
		// TODO daha duzgun exception handling yapilacak
		games = new HashMap<String, Integer>();
		nick = null;
		gui = aGui;
		gui.start(this); // TODO gozden gecir

		try {
			sock = new Socket(server, port);
			outputStream = new Formatter(new OutputStreamWriter(
					sock.getOutputStream()));
			inputStream = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));

			// Server'dan gelen baglantilari dinleyen thread
			Thread listener = new Thread(new Runnable() {
				public void run() {
					String input;

					try {
						while ((input = inputStream.readLine()) != null) {
							handleInput(input);
						}
					} catch (EOFException e) {
						gui.displayError("Connection lost.");
					} catch (IOException e) {
						gui.displayError(e.getMessage());
					}
				}
			});
			listener.start();

			sendCommand(String.format("NICK SET %s", aNick));
			sendCommand("NAMES");

		} catch (UnknownHostException e) {
			gui.displayError(e.getMessage());
		} catch (IOException e) {
			gui.displayError(e.getMessage());
		}
	}

	public void sendCommand(String cmd) {
		// TODO format'i daha verimli kullanmanin yollarini dusun
		// public olmasi sorun yaratabilir.
		outputStream.format("%s\n", cmd);
		outputStream.flush();
	}

	private void handleInput(String input) {
		if (input == null)
			return;
		// TODO exception handling
		String cmd = "cmd" + input.split(" ")[0].trim();
		String rest = input.substring(input.indexOf(' ')).trim();
		Class<? extends Client> c = this.getClass();
		try {
			Method m = c.getDeclaredMethod(cmd, String.class);
			m.invoke(this, rest);
		} catch (SecurityException e) {
			gui.displayError(e.getMessage());
		} catch (NoSuchMethodException e) {
			gui.displayError(e.getMessage());
		} catch (IllegalArgumentException e) {
			gui.displayError(e.getMessage());
		} catch (IllegalAccessException e) {
			gui.displayError(e.getMessage());
		} catch (InvocationTargetException e) {
			gui.displayError(e.getMessage());
		}
	}

	/********************
	 * PROTOCOL METHODS *
	 ********************/

	// warning vermesi cok gicik
	@SuppressWarnings("unused")
	private void cmdNAMES(String arg) {
		onlines = arg.split(",");
		gui.updateOnlineNicks(onlines);
	}

	@SuppressWarnings("unused")
	private void cmdMSG(String arg) {
		String[] arr = arg.split(" ", 2);
		String from = arr[0];
		String msg = arr[1];
		gui.displayMessage(from, msg, false);
	}

	@SuppressWarnings("unused")
	private void cmdPRIVMSG(String arg) {
		String[] arr = arg.split(" ", 3);
		// to = arr[0]
		// Mesaj bana geldigine gore to benimdir. Ilerde dogrulugu test
		// edilebilir.
		String from = arr[1];
		String msg = arr[2];

		gui.displayMessage(from, msg.toString(), true);
	}

	@SuppressWarnings("unused")
	private void cmdGAMES(String arg) {
		games = new HashMap<String, Integer>();
		// Oyun eksilirse diye yeniden hashmap olusturduk.
		String[] game = arg.split(",");
		for (int i = 0; i < game.length; i++) {
			String name = game[i].split("=")[0].trim();
			Integer numofplayer = new Integer(game[i].split("=")[1].trim());
			games.put(name, numofplayer);
		}
		gui.setAvailableGames((String[]) games.keySet().toArray());
	}

	@SuppressWarnings("unused")
	private void cmdNICK(String arg) {
		String status = arg.split(" ")[0];
		String nickName = arg.split(" ")[1];
		if (status.equals("ACCEPTED"))
			nick = nickName;
		else
			gui.displayError("Your nick isn't accepted.");
	}

	@SuppressWarnings("unused")
	private void cmdERR(String arg) {
		gui.displayError(arg);
	}

	@SuppressWarnings("unused")
	private void cmdINFO(String arg) {
		gui.displayServerMessage(arg);
	}

	/***************
	 * GUI METHODS *
	 ***************/

	/**
	 * Server ile olan baglantinin kesilmesini saglar
	 */
	public void disconnect() {
		// TODO program calismaya devam ediyor.
		sendCommand("DISCONNECT");
		try {
			inputStream.close();
			outputStream.close();
			sock.close();
		} catch (IOException e) {
			// daha detayli birsey yapilabilir
			System.err
					.println("Unexpected error happened while disconnecting...");
		} finally {
			System.exit(0);
		}
	}

	/**
	 * Mesajin server'a iletilmesini saglar. Eger basinda to:[nick] seklinde bir
	 * ifade varsa ozel mesaj olarak gonderir.
	 * 
	 * @param message
	 *            server'a iletilecek mesaj
	 */
	public void sendMessage(String message) {
		// TODO ozel mesaj
		if (message.contains("to:")) {
			String to = message.substring(message.indexOf("to:") + 3,
					message.indexOf(' '));
			message = message.substring(message.indexOf(' '));
			sendCommand(String.format("PRIVMSG %s %s %s", to, nick, message));
		} else
			sendCommand(String.format("MSG %s %s", nick, message));
	}

	/**
	 * Oyuncuyu sectigi oyunun sirasina sokar
	 * 
	 * @param game
	 *            secilen oyunun adi
	 */
	public void joinQueue(String game) {
		sendCommand(String.format("JOIN %s", game));
	}

	/**
	 * Insanlari belirli bir oyuna davet etmeye yarar. Boylelikle sira
	 * bekleneden oyun baslayabilir.
	 * 
	 * @param game
	 *            oynanmak istenen oyun
	 * @param people
	 *            davet edilen insanlar (not: oynanacak oyunun gerektirdigi
	 *            oyuncu sayisina gore degisir.)
	 */
	public void invate(String game, String[] people) {
		// TODO oyunlar ve oyuncu sayilari tutulacak, kontrol edilecek
		int numOfPlayer = games.get(game);
		if (people.length != numOfPlayer)
			gui.displayError(String
					.format("%s oyunu %d oyuncu ile oynanmaktadir. Lutfen secimlerinizi gozden gecirerek tekrar yapiniz.",
							game, numOfPlayer));
		else {
			String cmd = String.format("INVATE %s", game);
			for (int i = 0; i < people.length; i++) {
				cmd += " " + people[i];
			}
			sendCommand(cmd);
		}
	}

	/**
	 * Kullanicinin nick'ini degistirmesini saglayan method.
	 * 
	 * @param nick
	 *            yeni nick
	 */
	public void setNick(String nick) {
		sendCommand(String.format("NICK SET %s", nick));
	}

	/********** MAIN **********/
	public static void main(String[] args) {
		new Client("localhost", 5001, "mustafa", new SwingWaitingRoom());
	}
}
