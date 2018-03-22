package pop3.client.states;

import pop3.client.Client;
import pop3.client.Packet;

import java.util.LinkedList;
import java.util.List;

public class WaitingBeforeSendingEmailContentState extends State {
    public WaitingBeforeSendingEmailContentState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        this.client.setState(new EmailContentSentState(this.client));
        // TODO
        this.client.sendPacket(new Packet(this.client.getMessage().getBody() + "\n.\n"));
    }
}
