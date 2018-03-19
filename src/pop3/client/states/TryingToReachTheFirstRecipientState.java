package pop3.client.states;

import pop3.client.Client;
import pop3.client.Packet;

public class TryingToReachTheFirstRecipientState extends State {
    public TryingToReachTheFirstRecipientState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        if (result.startsWith("250")) {
            // TODO
            this.client.setState(new TryingToReachNextRecipientsState(this.client));
        } else {
            // TODO
        }
    }
}
