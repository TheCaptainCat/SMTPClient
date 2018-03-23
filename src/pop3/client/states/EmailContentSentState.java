package pop3.client.states;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

public class EmailContentSentState extends State {
    public EmailContentSentState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        this.client.setState(new WritingState(this.client));
        this.client.notifyGUI(
            new Notification(
                NotificationType.ENDED,
                null
            )
        );
    }
}
