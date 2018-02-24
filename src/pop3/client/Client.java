package pop3.client;

import pop3.client.Commands.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
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

    private String username;
    private String password;

    private Command lastCommand;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        this.connected = false;
        this.loggedIn = false;
        this.lastCommand = null;
    }

    public void connect(String username, String password) {
        this.username = username;
        this.password = password;
        this.lastCommand = new ConnectionCommand(this);

        Socket socket = null;
        try {
            InetAddress server = InetAddress.getByName(address);
            socket = new Socket(server, port);
        } catch (IOException e) {
            this.setConnected(false);
            ((ConnectionCommand)this.lastCommand).connectionFailed();
        }

        this.receiver = new Receiver(socket);
        this.receiver.addObserver(this);
        this.sender = new Sender(socket);
        this.sender.addObserver(this);

        new Thread(this.receiver).start();
        new Thread(this.sender).start();
    }

    public void setConnected(boolean value) {
        this.connected = value;
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

    public synchronized void performApop() {
        this.performApop(this.username, this.password);
    }

    public synchronized void performApop(String username, String password) {
        this.lastCommand = new ApopCommand(this);
        this.sender.sendPacket(new Packet(String.format("APOP %s %s", username, Hasher.getHash(password))));
    }

    public synchronized void listMessages() {
        this.lastCommand = new ListCommand(this);
        this.sender.sendPacket(new Packet("LIST"));
    }

    public synchronized void retreiveMessages(List<String> ids) {

    }

    public synchronized void retreiveMessage(String id) {
        this.lastCommand = new RetrCommand(this);
        this.sender.sendPacket(new Packet(String.format("RETR %s", id)));
    }

    public void notifyGUI(Notification notification) {
        setChanged();
        notifyObservers(notification);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(arg);
        this.lastCommand.handleResult((String) arg);
    }
}
