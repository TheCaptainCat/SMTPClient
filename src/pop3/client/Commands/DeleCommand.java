package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

public class DeleCommand extends Command {
    public DeleCommand(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        this.error = !result.startsWith("+OK");
        this.client.notifyGUI(
                new Notification(
                        this.error ? NotificationType.DELE_FAILED : NotificationType.DELE_OK,
                        null
                )
        );
    }
}
