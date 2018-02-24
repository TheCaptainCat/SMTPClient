package pop3.client.Commands;

import pop3.client.Client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListCommand extends Command {

    private static final int NOT_SET = -1;

    private int numberOfMessages;

    public ListCommand(Client client) {
        super(client);
        this.numberOfMessages = NOT_SET;
    }

    @Override
    public void handleResult(String result) {
        if (this.numberOfMessages == NOT_SET) {
            Pattern p = Pattern.compile("\\+OK (\\d+) messages");
            Matcher m = p.matcher(result);

            if (m.matches()) {
                this.numberOfMessages = Integer.parseInt(m.group(1));
                this.error = false;
            } else {
                this.error = true;
            }
        } else {
            this.result.add(result);
            if (this.result.size() == this.numberOfMessages) {
                this.client.retrieveMessages(this.result);
            }
        }
    }
}
