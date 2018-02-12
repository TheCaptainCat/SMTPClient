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

    public Client() {

    }

    public synchronized void setConnection(Connection c) {
        c.addObserver(this);
        new Thread(c).start();
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(o);
    }
}