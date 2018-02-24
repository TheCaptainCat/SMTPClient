package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

public class ApopCommand extends Command {

    public ApopCommand(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        this.error = !result.startsWith("+OK");
        this.client.setLoggedIn(!this.error);
        this.client.notifyGUI(
                new Notification(
                        this.error ? NotificationType.APOP_FAILED : NotificationType.APOP_OK,
                        null
                )
        );
    }
}
