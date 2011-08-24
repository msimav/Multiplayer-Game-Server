package client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.FlowLayout;

public class WaitingRoom implements client.GraphicalUserInterface {

    protected Shell shlWaitingRoom;
    private Combo combo;
    private List list;
    private StyledText styledText;
    private Text text;

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            WaitingRoom window = new WaitingRoom();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shlWaitingRoom.open();
        shlWaitingRoom.layout();
        while (!shlWaitingRoom.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shlWaitingRoom = new Shell();
        shlWaitingRoom.setSize(680, 500);
        shlWaitingRoom.setText("Waiting Room");
        shlWaitingRoom.setLayout(new GridLayout(2, false));

        Menu menu = new Menu(shlWaitingRoom, SWT.BAR);
        shlWaitingRoom.setMenuBar(menu);

        MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
        mntmFile.setText("File");

        Menu menu_1 = new Menu(mntmFile);
        mntmFile.setMenu(menu_1);

        MenuItem mntmQuit = new MenuItem(menu_1, SWT.NONE);
        mntmQuit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                quit();
            }
        });
        mntmQuit.setText("Quit");

        MenuItem mntmAbout = new MenuItem(menu, SWT.NONE);
        mntmAbout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        mntmAbout.setText("About");

        Composite composite = new Composite(shlWaitingRoom, SWT.NONE);
        composite.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        GridData gd_composite = new GridData(SWT.FILL, SWT.TOP, false, false,
                1, 1);
        gd_composite.heightHint = 35;
        composite.setLayoutData(gd_composite);

        Label lblQueue = new Label(composite, SWT.NONE);
        lblQueue.setText("Queue: ");

        combo = new Combo(composite, SWT.READ_ONLY);
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                queueSelected();
            }
        });
        combo.setItems(new String[] { "Satranç", "Tic Toc Toe", "Tavla" });
        new Label(shlWaitingRoom, SWT.NONE);

        ScrolledComposite scrolledComposite = new ScrolledComposite(
                shlWaitingRoom, SWT.BORDER | SWT.H_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
                true, 1, 1));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        styledText = new StyledText(scrolledComposite, SWT.FULL_SELECTION
                | SWT.READ_ONLY);
        styledText.setText("deneme");
        scrolledComposite.setContent(styledText);
        scrolledComposite.setMinSize(styledText.computeSize(SWT.DEFAULT,
                SWT.DEFAULT));

        ScrolledComposite scrolledComposite_1 = new ScrolledComposite(
                shlWaitingRoom, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite_1.setMinWidth(150);
        scrolledComposite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                false, true, 1, 1));
        scrolledComposite_1.setExpandHorizontal(true);
        scrolledComposite_1.setExpandVertical(true);

        list = new List(scrolledComposite_1, SWT.H_SCROLL | SWT.MULTI);
        scrolledComposite_1.setContent(list);
        scrolledComposite_1.setMinSize(new Point(150, 150));

        text = new Text(shlWaitingRoom, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Button btnNewButton = new Button(shlWaitingRoom, SWT.CENTER);
        btnNewButton.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                sendMessage();
            }
        });
        btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
                false, 1, 1));
        btnNewButton.setText("Send");

    }

    public void displayMessage(String from, String message, boolean isPrivate) {

    }

    public void updateOnlineNicks(String[] list) {

    }

    private void queueSelected() {
        // TODO join the queue
    }

    private void sendMessage() {
        // TODO send message to client
    }

    private void quit() {
        // TODO send disconnect signal
    }
}
