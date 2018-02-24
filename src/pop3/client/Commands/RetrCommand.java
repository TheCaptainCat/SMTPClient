package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Notification;
import pop3.client.NotificationType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrCommand extends Command {

    private static final int NOT_SET = -1;

    private int numberOfMessages;

    public RetrCommand(Client client) {
        super(client);
        this.numberOfMessages = NOT_SET;
    }

    @Override
    public void handleResult(String result) {
        if (this.numberOfMessages == NOT_SET) {
            Pattern p = Pattern.compile("\\+OK (\\d+) octets");
            Matcher m = p.matcher(result);

            if (m.matches()) {
                this.numberOfMessages = Integer.parseInt(m.group(1));
                this.error = false;
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
                this.client.setRetreivedMessage(this.result.get(0));
            }
        }
    }
}
