package pop3.client.Commands;

public class ConnectionCommand extends Command {
    @Override
    public void handleResult(String result) {
        this.error = !result.startsWith("+OK POP3 server ready");
    }
}
