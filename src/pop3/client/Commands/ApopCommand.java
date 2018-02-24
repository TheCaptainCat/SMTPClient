package pop3.client.Commands;

import pop3.client.Client;

public class ApopCommand extends Command {

    private Client client;

    public ApopCommand(Client client) {
        this.client = client;
    }

    @Override
    public void handleResult(String result) {
        this.error = !result.startsWith("+OK");
        this.client.setLoggedIn(!this.error);
    }
}
