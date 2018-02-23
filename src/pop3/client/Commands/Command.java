package pop3.client.Commands;

public abstract class Command {

    protected boolean error;

    public Command() {
        this.error = false;
    }

    public abstract void handleResult(String result);

    public boolean isError() {
        return this.error;
    }
}
