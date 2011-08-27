package client;

public interface GraphicalUserInterface {

    /**
     * Client'in aldigi mesajin ekranda goruntulenmesini saglar.
     * 
     * @param from
     *            mesajin kimden geldigi
     * @param message
     *            mesajin icerigi
     * @param isPrivate
     *            ozel mesaj olup olmadigi
     */
    public void displayMessage(String from, String message, boolean isPrivate);

    /**
     * Sunucuya bagli olan kisilerin nicklerini gunceller
     * 
     * @param list
     */
    public void updateOnlineNicks(String[] list);

    /**
     * Alinan hata mesajinin ekranda goruntulenmesini saglar
     * 
     * @param message
     *            hata mesaji
     */
    public void displayError(String message);

    /**
     * Serverdan gelen cesitli mesajlarin/uyarilarin ekranda goruntulenmesini
     * saglar
     * 
     * @param message
     *            server mesaji
     */
    public void displayServerMessage(String message);

    /**
     * Server tarafindaki aktif oyunlarin listesini ekranda gosterir. Boylece
     * oyuncu bu oyunlardan birini secerek sirasina girer.
     * 
     * @param games
     *            oyunlarin listesi
     */
    public void setAvailableGames(String[] games);

    /**
     * Client, gui'ye kendisini gondererek guinin baslamasini saglar.
     * 
     * @param aClient
     *            client'in referansi
     */
    public void start(Client aClient);
}
