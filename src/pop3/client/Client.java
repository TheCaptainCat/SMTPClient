package pop3.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

/**
 * Une connexion permet de faire une requête vers le serveur et attend la réponse.
 */
public class Client extends Observable implements Observer {

    private Receiver receiver;
    private Sender sender;

    public Client(String address, int port) throws IOException {
        InetAddress server = InetAddress.getByName(address);
        Socket socket = new Socket(server, port);
        this.receiver = new Receiver(socket);
        this.sender = new Sender(socket);

        new Thread(this.receiver).start();
        new Thread(this.sender).start();
    }

    public synchronized void stop(boolean run) {
        this.receiver.stop();
        this.sender.stop();
    }

    public synchronized void performApop(String username, String password) {
        this.sender.sendPacket(new Packet(String.format("APOP %s %s", username, Hasher.getHash(password))));
    }

    public synchronized void fetchMessages() {
        this.sender.sendPacket(new Packet("LIST"));
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }
}
