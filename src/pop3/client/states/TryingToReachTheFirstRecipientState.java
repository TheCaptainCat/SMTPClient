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
            String recipient = this.client.getMessage().popRecipient();
            if (recipient != null) {
                this.client.setState(new TryingToReachNextRecipientsState(this.client));
                this.client.sendPacket(new Packet(String.format("RCPT TO:<%s>", recipient)));
            } else {
                this.client.setState(new WaitingBeforeSendingEmailContentState(this.client));
                this.client.sendPacket(new Packet("DATA"));
            }
        } else {
            // TODO
        }
    }
}
