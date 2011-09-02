package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import client.Client;

/**
 * TODO ozel mesaj gonderme ayarlanacak
 */
public class SwingWaitingRoom implements client.GraphicalUserInterface {

	private Client client;

	private JFrame frame;
	private JPanel contentPane;
	private JTextField messageField;
	private JComboBox gameList;
	private JList onlineList;
	private JTextPane chatPane;
	private StyledDocument messages;

	/**
	 * Create the application.
	 */
	public SwingWaitingRoom() {
		initialize();
		initilazeStyles();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(650, 450));
		frame.setSize(new Dimension(650, 450));
		frame.setTitle("Waiting Room");
		frame.setBounds(100, 100, 647, 452);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				quit();
			}
		});

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);

		JMenuItem mntmRunCommand = new JMenuItem("Run Command");
		mntmRunCommand.setMnemonic('R');
		mntmRunCommand.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				Event.ALT_MASK));
		mntmRunCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runCommand();
			}
		});
		mnFile.add(mntmRunCommand);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setMnemonic('Q');
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				Event.CTRL_MASK));
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		mnFile.add(mntmQuit);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setMnemonic('A');
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				about();
			}
		});
		mnHelp.add(mntmAbout);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.ipady = 5;
		gbc_panel.ipadx = 5;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		contentPane.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JLabel lblQueue = new JLabel("Queue: ");
		panel.add(lblQueue);

		gameList = new JComboBox();
		gameList.setEditable(false);
		panel.add(gameList);

		JButton btnJoin = new JButton("Join");
		btnJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinGameQueue();
			}
		});
		panel.add(btnJoin);

		JButton btnInvate = new JButton("Invate");
		btnInvate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				invatePeople();
			}
		});
		panel.add(btnInvate);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weightx = 580.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);

		messages = new DefaultStyledDocument();
		chatPane = new JTextPane(messages);
		scrollPane.setViewportView(chatPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.weightx = 70.0;
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 1;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);

		onlineList = new JList();
		onlineList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listSelectionListener();
			}
		});
		scrollPane_1.setViewportView(onlineList);

		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					sendMessage();
			}
		});
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 0, 5);
		gbc_textField.fill = GridBagConstraints.BOTH;
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 2;
		contentPane.add(messageField, gbc_textField);
		messageField.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
	}

	/**
	 * Bu guide kullanilacak stillerin tanimlandigi method.
	 */
	private void initilazeStyles() {
		Style base = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);

		// ERROR MESSAGES STYLE
		Style error = messages.addStyle("error", base);
		StyleConstants.setBold(error, true);
		StyleConstants.setForeground(error, new Color(0x800000));

		// INFORMATION MESSAGES STYLE
		Style info = messages.addStyle("info", base);
		StyleConstants.setItalic(info, true);
		StyleConstants.setForeground(info, new Color(0x006400));

		// MESSAGE STYLE
		messages.addStyle("message", base);
		Style privmsg = messages.addStyle("privmsg", base);
		StyleConstants.setItalic(privmsg, true);
	}

	private String getDate() {
		String date = new Date().toString();
		int index = date.indexOf(':');
		date = '[' + date.substring(index - 2, index + 6) + ']';
		return date;
	}

	private Color getNickColor(String nick) {
		int hash = nick.hashCode();
		// int red, green, blue;
		// blue = hash & 255;
		// green = (hash & 65280) >>> 8;
		// red = (hash & 16711680) >>> 16;
		return new Color(hash & 0xFFFFFF);
	}

	/******************
	 * CLIENT METHODS *
	 ******************/

	@Override
	public void displayMessage(String from, String message, boolean isPrivate) {
		// if style for the nick is not exist
		if (messages.getStyle(from) == null) {
			// message
			Style newStyle = messages.addStyle(from,
					messages.getStyle("message"));
			StyleConstants.setForeground(newStyle, getNickColor(from));
			// private message
			newStyle = messages.addStyle("#Priv_" + from,
					messages.getStyle("privmsg"));
			StyleConstants.setForeground(newStyle, getNickColor(from));
		}

		try {
			String nickStyle = from, messageStyle = "message";
			if (isPrivate) {
				nickStyle = "#Priv_" + nickStyle;
				messageStyle = "privmsg";
			}
			// nick
			messages.insertString(messages.getLength(), from,
					messages.getStyle(nickStyle));
			// date and message
			messages.insertString(messages.getLength(),
					String.format("%s > %s\n", getDate(), message),
					messages.getStyle(messageStyle));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void updateOnlineNicks(String[] list) {
		onlineList.setListData(list);
		// selection listener ile sorun olusturabilir.
		onlineList.setSelectedIndex(-1);
	}

	@Override
	public void displayError(String message) {
		try {
			messages.insertString(messages.getLength(),
					String.format("%s\n", message), messages.getStyle("error"));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void displayServerMessage(String message) {
		try {
			messages.insertString(messages.getLength(),
					String.format("%s\n", message), messages.getStyle("info"));
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setAvailableGames(String[] games) {
		gameList.removeAllItems();
		for (int i = 0; i < games.length; i++) {
			gameList.addItem(games[i]);
		}
	}

	@Override
	public void start(Client aClient) {
		client = aClient;
		frame.setVisible(true);
	}

	/********************
	 * LISTENER METHODS *
	 ********************/

	/**
	 * Mesaj gonderilecegi zaman calisacak method, mesaji client'a iletecek
	 */
	private void sendMessage() {
		client.sendMessage(messageField.getText());
		messageField.setText("");
		messageField.requestFocus();
	}

	/**
	 * Eger tek kisi seciliyse ozel mesaj attiracak listener.
	 */
	private void listSelectionListener() {
		// TODO Ozel mesaj olayina karar ver ve tekrar gozden gecir.
		String msg = messageField.getText();
		if (msg.contains("to:"))
			msg = msg.split(" ", 2)[1];

		if (onlineList.getSelectedIndices().length == 1) {
			messageField.setText(String.format("to:%s %s",
					(String) onlineList.getSelectedValue(), msg));
		} else {
			messageField.setText(msg);
		}
		messageField.requestFocus();
	}

	/**
	 * Oyun secildiginde calisacak method, client'taki gerekli methodlari
	 * cagirarak server'a gerekli bilgiye gonderecek
	 */
	private void joinGameQueue() {
		if (gameList.getSelectedIndex() != -1) {
			client.joinQueue((String) gameList.getSelectedItem());
		} else {
			JOptionPane.showMessageDialog(null,
					"You must select a game to join its queue.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Secili oyuna secili insanlari davet eder
	 */
	private void invatePeople() {
		// TODO oyuncu sayisi kontrolu
		if (gameList.getSelectedIndex() != -1) {
			client.invate((String) gameList.getSelectedItem(),
					(String[]) onlineList.getSelectedValues());
		} else {
			JOptionPane.showMessageDialog(null,
					"You must select a game to join its queue.", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Program kapatildiginda calisacak method, server'a gerekli bilgi
	 * gonderilecek
	 */
	private void quit() {
		frame.setVisible(false);
		frame.dispose();
		client.disconnect();
		System.exit(0);
	}

	/**
	 * About ekrani
	 */
	private void about() {
		String title = "About";
		String msg = "Error! Keyboard not found.\nPress Enter to continue.";
		JOptionPane.showMessageDialog(null, msg, title,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Isini bilen kullanicilar icin server'a direkt komut gondermeye yarayan
	 * method
	 */
	private void runCommand() {
		String cmd = JOptionPane.showInputDialog(null,
				"Calistirilacak komutu girin", "Run Command",
				JOptionPane.PLAIN_MESSAGE);
		if (cmd != null)
			client.sendCommand(cmd);
	}
}
