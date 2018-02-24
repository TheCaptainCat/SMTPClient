package pop3.client;

import pop3.client.Commands.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
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

    private boolean isRetrievingMessages;
    private List<String> messageIdsToRetrieve;
    private int messageIndexToRetrieve;
    private List<Message> retrieveMessagesResult;

    private Command lastCommand;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        this.connected = false;
        this.loggedIn = false;
        this.lastCommand = null;
        this.isRetrievingMessages = false;
        this.messageIndexToRetrieve = 0;
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

    public synchronized void retrieveMessages(List<String> ids) {
        if (ids.size() > 0) {
            this.isRetrievingMessages = true;
            this.retrieveMessagesResult = new LinkedList<>();
            this.messageIdsToRetrieve = ids;
            this.retrieveMessage(ids.get(0));
            this.messageIndexToRetrieve = 1;
        }
    }

    public synchronized void retrieveMessage(String id) {
        this.lastCommand = new RetrCommand(this);
        this.sender.sendPacket(new Packet(String.format("RETR %s", id)));
    }

    public synchronized void setRetreivedMessage(Message message) {
        if (this.isRetrievingMessages) {
            this.retrieveMessagesResult.add(message);
            this.retrieveNextMessage();
        }
    }

    public synchronized void retrieveNextMessage() {
        if (this.isRetrievingMessages) {
            if (this.messageIndexToRetrieve < this.messageIdsToRetrieve.size()) {
                this.retrieveMessage(this.messageIdsToRetrieve.get(this.messageIndexToRetrieve));
                this.messageIndexToRetrieve++;

            } else {
                this.isRetrievingMessages = false;
                setChanged();
                notifyGUI(new Notification(
                        NotificationType.RETR_ALL_MESSAGES_OK,
                        this.retrieveMessagesResult)
                );
            }
        }
    }



    public void notifyGUI(Notification notification) {
        setChanged();
        notifyObservers(notification);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Received : " + arg);
        this.lastCommand.handleResult((String) arg);
    }
}
