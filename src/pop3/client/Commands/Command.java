package pop3.client.Commands;

import pop3.client.Client;

import java.util.LinkedList;
import java.util.List;

public abstract class Command {

    protected Client client;

    protected boolean error;

    public List<String> result;

    public Command(Client client) {
        this.client = client;
        this.error = false;
        this.result = new LinkedList<>();
    }

    public abstract void handleResult(String result);

    public boolean isError() {
        return this.error;
    }
}
