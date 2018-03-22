package pop3.client;

import pop3.client.states.State;
import pop3.client.states.WritingState;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

/**
 * Une connexion permet de faire une requête vers le serveur et attend la réponse.
 */
public class Client extends Observable implements Observer {

    private State state;
    private Message message;
    private String domain;

    private Receiver receiver;
    private Sender sender;

    private String address;
    private int port;

    public Client(String address, int port, String domain) {
        this.address = address;
        this.port = port;
        this.state = new WritingState(this);
        this.domain = domain;
    }

    public void connect() {

        Socket socket = null;
        try {
            InetAddress server = InetAddress.getByName(address);
            socket = new Socket(server, port);
        } catch (Exception e) {
            notifyGUI(new Notification(NotificationType.CONNECTION_FAILED, null));
            return;
        }

        this.receiver = new Receiver(socket);
        this.receiver.addObserver(this);
        this.sender = new Sender(socket);
        this.sender.addObserver(this);

        new Thread(this.receiver).start();
        new Thread(this.sender).start();
    }

    public String getDomain() {
        return domain;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void sendPacket(Packet packet) {
        this.sender.sendPacket(packet);
    }

    public synchronized void stop(boolean run) {
        this.receiver.stop();
        this.sender.stop();
    }

    public void notifyGUI(Notification notification) {
        setChanged();
        notifyObservers(notification);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Received : " + arg);
        this.state.handleResult(arg.toString());
    }
}
