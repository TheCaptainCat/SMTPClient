package pop3.client;

import java.util.Observable;

/**
 * Une connexion permet de faire une requête vers le serveur et attend la réponse.
 */
public class Connection extends Observable implements Runnable {

    private String address;
    private String port;

    public Connection(String address, String port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        // TODO: Se connecter
        setChanged();
        notifyObservers();
    }
}