package pop3.client.states;

import pop3.client.Client;
import pop3.client.Packet;

public class ExtensionsReceivedState extends State {
    public ExtensionsReceivedState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        if (result.startsWith("250")) {
            this.client.setState(new TryingToReachTheFirstRecipientState(this.client));
            this.client.sendPacket(new Packet(String.format("RCPT TO:<%s>", this.client.getMessage().popRecipient())));
        }
    }
}
