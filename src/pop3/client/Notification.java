package pop3.client;

public class Notification {
    private NotificationType type;
    private Object arguments;

    public Notification(NotificationType type, Object arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Object getArguments() {
        return arguments;
    }

    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }
}
