package pop3.client.states;

import pop3.client.Client;

public class EmailContentSentState extends State {
    public EmailContentSentState(Client client) {
        super(client);
    }

    @Override
    public void handleResult(String result) {
        this.client.setState(new WritingState(this.client));
    }
}
