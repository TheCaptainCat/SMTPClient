package pop3.client.states;

import pop3.client.Client;
import pop3.client.Packet;

public class TryingToReachNextRecipientsState extends State {
    public TryingToReachNextRecipientsState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        // TODO empty list
        // If empty list then
        this.client.setState(new WaitingBeforeSendingEmailContentState(this.client));
        this.client.sendPacket(new Packet("DATA"));
        // End if
    }
}
