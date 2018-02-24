package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrCommand extends Command {

    private boolean hasRecievedOk;

    public RetrCommand(Client client) {
        super(client);
        this.hasRecievedOk = false;
    }

    @Override
    public void handleResult(String result) {
        if (!this.hasRecievedOk) {
            if (result.startsWith("+OK")) {
                this.hasRecievedOk = true;
            } else {
                this.error = true;
                this.client.notifyGUI(
                        new Notification(
                                NotificationType.RETR_FAILED,
                                null
                        )
                );
            }
        } else {
            if (!result.equals(".")) {
                this.result.add(result);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < this.result.size(); i++) {
                    sb.append(this.result.get(i)).append('\n');
                }
                this.client.setRetreivedMessage(sb.toString());
            }
        }
    }
}
