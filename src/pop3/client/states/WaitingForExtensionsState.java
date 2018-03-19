package pop3.client.states;

import pop3.client.Client;
import pop3.client.Packet;

import java.util.LinkedList;
import java.util.List;

public class WaitingForExtensionsState extends State {
    private List<String> supportedExtensions;

    public WaitingForExtensionsState(Client client) {
        super(client);
        this.supportedExtensions = new LinkedList<>();
    }

    @Override
    public void handleResult(String result) {
        // TODO supportedExtensions
        this.supportedExtensions.add(result);
        this.client.setState(new ExtensionsReceivedState(this.client));
        this.client.sendPacket(new Packet(String.format("MAIL FROM:<%s>", this.client.getMessage().getFrom())));
    }
}
