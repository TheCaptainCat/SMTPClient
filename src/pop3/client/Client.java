package pop3.client;

import pop3.client.Commands.ApopCommand;
import pop3.client.Commands.Command;
import pop3.client.Commands.ConnectionCommand;
import pop3.client.Commands.ListCommand;

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

    private String address;
    private int port;

    private boolean connected;
    private boolean loggedIn;

    private Command lastCommand;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        this.connected = false;
        this.loggedIn = false;
        this.lastCommand = null;
    }

    public boolean connect() {
        this.lastCommand = new ConnectionCommand();

        Socket socket = null;
        try {
            InetAddress server = InetAddress.getByName(address);
            socket = new Socket(server, port);
        } catch (IOException e) {
            return false;
        }

        this.receiver = new Receiver(socket);
        this.receiver.addObserver(this);
        this.sender = new Sender(socket);
        this.sender.addObserver(this);

        new Thread(this.receiver).start();
        new Thread(this.sender).start();
        this.connected = true;

        return true;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean value) {
        this.loggedIn = value;
    }

    public synchronized void stop(boolean run) {
        this.receiver.stop();
        this.sender.stop();
    }

    public synchronized void performApop(String username, String password) {
        this.sender.sendPacket(new Packet(String.format("APOP %s %s", username, Hasher.getHash(password))));
        this.lastCommand = new ApopCommand(this);
    }

    public synchronized void fetchMessages() {
        this.sender.sendPacket(new Packet("LIST"));
        this.lastCommand = new ListCommand();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.lastCommand.handleResult((String) arg);
        setChanged();
        notifyObservers(this.lastCommand);
    }
}
