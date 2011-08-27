package client.gui;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.FlowLayout;
import client.Client;

public class WaitingRoom implements client.GraphicalUserInterface, Runnable {

    private client.Client client;

    protected Shell shlWaitingRoom;
    private Combo gameList;
    private List onlineList;
    private StyledText chatBox;
    private Text messageBox;

    /**
     * Open the window.
     */
    public void run() {
        Display display = Display.getDefault();
        createContents();
        shlWaitingRoom.open();
        shlWaitingRoom.layout();
        while (!shlWaitingRoom.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        // TODO C-q ile cikis eklenecek
        shlWaitingRoom = new Shell();
        shlWaitingRoom.setMinimumSize(new Point(675, 500));
        shlWaitingRoom.addShellListener(new ShellAdapter() {
            // Pencere kapatildiginda calisan listener
            public void shellClosed(ShellEvent e) {
                quit();
            }
        });
        shlWaitingRoom.setSize(675, 500);
        shlWaitingRoom.setText("Waiting Room");
        GridLayout gl_shlWaitingRoom = new GridLayout(2, false);
        shlWaitingRoom.setLayout(gl_shlWaitingRoom);

        Menu menu = new Menu(shlWaitingRoom, SWT.BAR);
        shlWaitingRoom.setMenuBar(menu);

        MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
        mntmFile.setText("File");

        Menu menu_1 = new Menu(mntmFile);
        mntmFile.setMenu(menu_1);

        MenuItem mntmQuit = new MenuItem(menu_1, SWT.NONE);
        mntmQuit.addSelectionListener(new SelectionAdapter() {
            // Quit butonuna tiklandiginda calisacak listener
            public void widgetSelected(SelectionEvent e) {
                quit();
            }
        });
        mntmQuit.setText("Quit");

        MenuItem mntmAbout = new MenuItem(menu, SWT.NONE);
        mntmAbout.addSelectionListener(new SelectionAdapter() {
            // About buttonuna tiklandiginda calisacak listener
            public void widgetSelected(SelectionEvent e) {
                // TODO daha da gelistirilecek
                MessageBox about = new MessageBox(shlWaitingRoom, SWT.OK);
                about.setText("Info");
                about.setMessage("Deneme 1 2 3");
                about.open();
            }
        });
        mntmAbout.setText("About");

        Composite composite = new Composite(shlWaitingRoom, SWT.NONE);
        composite.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, false,
                false, 2, 1);
        gd_composite.heightHint = 35;
        composite.setLayoutData(gd_composite);

        Label lblQueue = new Label(composite, SWT.NONE);
        lblQueue.setText("Queue: ");

        gameList = new Combo(composite, SWT.READ_ONLY);

        Button btnJoin = new Button(composite, SWT.NONE);
        btnJoin.addSelectionListener(new SelectionAdapter() {
            // Join butonuna tiklandiginda calisacak listener
            public void widgetSelected(SelectionEvent e) {
                joinGameQueue();
            }
        });
        btnJoin.setText("Join");

        Button btnInvate = new Button(composite, SWT.NONE);
        btnInvate.addSelectionListener(new SelectionAdapter() {
            // Invate butonuna basildiginda calisacak listener
            public void widgetSelected(SelectionEvent e) {
                invatePeople();
            }
        });
        btnInvate.setText("Invate");

        ScrolledComposite scrolledComposite = new ScrolledComposite(
                shlWaitingRoom, SWT.BORDER | SWT.H_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
                true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        chatBox = new StyledText(scrolledComposite, SWT.FULL_SELECTION
                | SWT.READ_ONLY | SWT.V_SCROLL);
        chatBox.setText("");
        scrolledComposite.setContent(chatBox);
        scrolledComposite.setMinSize(chatBox.computeSize(SWT.DEFAULT,
                SWT.DEFAULT));

        ScrolledComposite scrolledComposite_1 = new ScrolledComposite(
                shlWaitingRoom, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite_1.setMinWidth(150);
        scrolledComposite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                false, true, 1, 1));
        scrolledComposite_1.setExpandHorizontal(true);
        scrolledComposite_1.setExpandVertical(true);

        onlineList = new List(scrolledComposite_1, SWT.H_SCROLL | SWT.MULTI);
        onlineList.addSelectionListener(new SelectionAdapter() {
            // Liste secildiginde calisacak listener
            public void widgetSelected(SelectionEvent e) {
                // TODO liste secildiginde yapilacaklar
                String to = onlineList.getItem(onlineList.getSelectionIndex());
                String newMsg = String.format("to:%s %s", to,
                        messageBox.getText());
                messageBox.setText(newMsg);
            }
        });
        scrolledComposite_1.setContent(onlineList);
        scrolledComposite_1.setMinSize(new Point(150, 150));

        messageBox = new Text(shlWaitingRoom, SWT.BORDER);
        messageBox.addKeyListener(new KeyAdapter() {
            // Enter ile mesaj gondermeyi saglayan listener
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) // Enter = 13
                    sendMessage();
            }
        });
        messageBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
                1, 1));
        messageBox.setFocus();

        Button sendButton = new Button(shlWaitingRoom, SWT.CENTER);
        sendButton.addSelectionListener(new SelectionAdapter() {
            // Send butonuna tiklandiginda mesaj gondermeyi saglayan listener
            public void widgetSelected(SelectionEvent e) {
                sendMessage();
            }
        });
        sendButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false,
                1, 1));
        sendButton.setText("Send");

    }

    /**
     * Oyun secildiginde calisacak method, client'taki gerekli methodlari
     * cagirarak server'a gerekli bilgiye gonderecek
     */
    private void joinGameQueue() {
        // TODO siraya girdiginde yapilacak degisiklikler
        if (gameList.getSelectionIndex() == -1) {
            MessageBox error = new MessageBox(shlWaitingRoom, SWT.ICON_ERROR
                    | SWT.OK);
            error.setText("Error");
            error.setMessage("Oyun sirasina katilmak icin once oyun secmelisiniz.");
            error.open();
        } else
            client.joinQueue(gameList.getItem(gameList.getSelectionIndex()));
    }

    /**
     * Mesaj gonderilecegi zaman calisacak method, mesaji client'a iletecek
     */
    private void sendMessage() {
        // TODO ozel mesaj
        client.sendMessage(messageBox.getText());
        messageBox.setText("");
        messageBox.setFocus();
    }

    /**
     * Program kapatildiginda calisacak method, server'a gerekli bilgi
     * gonderilecek
     */
    private void quit() {
        client.disconnect();
        shlWaitingRoom.dispose();
    }

    /**
     * Secili oyuna secili insanlari davet eder
     */
    private void invatePeople() {
        if (gameList.getSelectionIndex() == -1) {
            MessageBox error = new MessageBox(shlWaitingRoom, SWT.ICON_ERROR
                    | SWT.OK);
            error.setText("Error");
            error.setMessage("Oyun sirasina katilmak icin once oyun secmelisiniz.");
            error.open();
        } else
            client.invate(gameList.getItem(gameList.getSelectionIndex()),
                    onlineList.getSelection());
    }

    /**
     * Mesaji gonderen kullanicinin nick'ini hash'leyip buna gore bir renk
     * uretir. Boylece her kullanicinin farkli bir rengi olur ve mesajlar daha
     * rahatlikla takip edilebilir.
     * 
     * @param nick
     *            mesaji gonderen kullanicinin nick'i
     * @return nick ile eslesen renk
     */
    private Color getNickColor(String nick) {
        int hash = nick.hashCode();
        int red, green, blue;
        blue = hash & 255;
        green = (hash & 65280) >>> 8;
        red = (hash & 16711680) >>> 16;
        return new Color(shlWaitingRoom.getDisplay(), new RGB(red, green, blue));
    }

    /**
     * Mesajin icindeki linkler bulunacak ve tiklanabilir hale donusturulecek
     * 
     * @param msg
     * @return
     */
    private StyleRange[] getLinks(String msg) {
        // TODO linkleri bulup link haline donusturecek
        // String regex =
        // "_^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\x{00a1}-\x{ffff}0-9]+-?)*[a-z\x{00a1}-\x{ffff}0-9]+)(?:\.(?:[a-z\x{00a1}-\x{ffff}0-9]+-?)*[a-z\x{00a1}-\x{ffff}0-9]+)*(?:\.(?:[a-z\x{00a1}-\x{ffff}]{2,})))(?::\d{2,5})?(?:/[^\s]*)?$_iuS";
        return null;
    }

    /**
     * Mesajin geldigi zamanin saat, dakika ve saniyesini dondurur.
     * 
     * @return "[HH:MM:SS]"
     */
    private String getDate() {
        String date = new Date().toString();
        int index = date.indexOf(':');
        date = '[' + date.substring(index - 2, index + 6) + ']';
        return date;
    }

    public void displayMessage(String from, String message, boolean isPrivate) {
        int nickStart = chatBox.getCharCount();
        int msgStart = nickStart + from.length() + 13;
        // nick
        StyleRange nick = new StyleRange();
        nick.start = nickStart;
        nick.length = from.length() + 13;
        nick.foreground = getNickColor(from);
        nick.fontStyle = isPrivate ? SWT.BOLD | SWT.ITALIC : SWT.BOLD;
        chatBox.append(from);
        chatBox.append(getDate());
        chatBox.append(" > ");
        chatBox.setStyleRange(nick);
        // message
        StyleRange msg = new StyleRange();
        msg.start = msgStart;
        msg.length = message.length();
        msg.fontStyle = isPrivate ? SWT.ITALIC : SWT.NORMAL;
        chatBox.append(message);
        chatBox.setStyleRange(msg);

    }

    public void updateOnlineNicks(String[] list) {
        onlineList.select(-1);
        onlineList.setItems(list);
    }

    public void displayError(String message) {
        StyleRange styleRange = new StyleRange();
        styleRange.start = chatBox.getCharCount();
        styleRange.length = message.length();
        styleRange.foreground = shlWaitingRoom.getDisplay().getSystemColor(
                SWT.COLOR_DARK_RED);
        styleRange.background = null;
        styleRange.fontStyle = SWT.BOLD;

        chatBox.append(message);
        chatBox.setStyleRange(styleRange);

    }

    public void displayServerMessage(String message) {
        StyleRange styleRange = new StyleRange();
        styleRange.start = chatBox.getCharCount();
        styleRange.length = message.length();
        styleRange.foreground = shlWaitingRoom.getDisplay().getSystemColor(
                SWT.COLOR_DARK_GREEN);
        styleRange.background = null;
        styleRange.fontStyle = SWT.ITALIC;

        chatBox.append(message);
        chatBox.setStyleRange(styleRange);

    }

    public void setAvailableGames(String[] games) {
        gameList.setItems(games);
    }

    public void start(Client aClient) {
        this.client = aClient;
        shlWaitingRoom.getDisplay().asyncExec(this);
        Thread guiThread = shlWaitingRoom.getDisplay().getThread();
        guiThread.start();

    }
}
