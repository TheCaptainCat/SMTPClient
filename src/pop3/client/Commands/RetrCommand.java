package pop3.client.Commands;

import pop3.client.Client;
import pop3.client.Message;
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
                Message message = new Message();
                for (int i = 0; i < this.result.size(); i++) {
                    String line = this.result.get(i);
                    if (line.startsWith("FROM")) {
                        message.setFrom(line);
                        System.out.println("from");
                    } else if (line.startsWith("TO")) {
                        message.setTo(line);
                    } else if (line.startsWith("CC")) {
                        message.setCc(line);
                    } else {
                        message.setBody(message.getBody() + '\n' + line);
                    }
                }
                this.client.setRetreivedMessage(message);
            }
        }
    }
}
