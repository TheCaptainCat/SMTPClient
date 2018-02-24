package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

public class ConnectionCommand extends Command {

    public ConnectionCommand(Client client) {
        super(client);
    }

    public void connectionFailed() {
        this.error = true;
        this.client.notifyGUI(new Notification(NotificationType.CONNECTION_FAILED, null));
    }

    @Override
    public void handleResult(String result) {
        this.error = !result.startsWith("+OK POP3 server ready");
        this.client.setConnected(!this.error);
        this.client.notifyGUI(
                new Notification(
                        this.error ? NotificationType.CONNECTION_FAILED : NotificationType.CONNECTION_OK,
                        null
                )
        );
        if (!this.error) {
            this.client.performApop();
        }
    }
}
