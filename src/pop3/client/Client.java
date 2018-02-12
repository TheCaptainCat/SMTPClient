package pop3.client;

import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Le client est concurent. Il permet d'envoyer des requêtes vers le serveur.
 */
public class Client extends Observable implements Observer {

    private Connection connection;

    public Client() {
        this.connection = null;
    }

    public synchronized void setConnection(Connection c) {
        this.connection = c;
        this.connection.addObserver(this);
        new Thread(this.connection).start();
    }

    public synchronized void fetchMessages() {
        this.connection.addString("LIST");
    }

    private synchronized void addMessage(String message) {
        this.connection.addString(message);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}