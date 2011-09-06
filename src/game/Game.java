package game;

import server.Server.Player;

public abstract class Game{

	private int numberOfPlayer;
	protected Player players[];
	
	public Game(int numberOfPlayer){
		this.numberOfPlayer = numberOfPlayer;
		players = new Player[numberOfPlayer];
	}
	
	public int getNumberOfPlayer() {
		return numberOfPlayer;
	}
	
	abstract protected void start();
	abstract protected void finish();
}
