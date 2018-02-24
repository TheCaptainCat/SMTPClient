package pop3.client;

public enum NotificationType {
    CONNECTION_FAILED,
    CONNECTION_OK,
    APOP_OK,
    APOP_FAILED,
    LIST_OK,
    LIST_FAILED,
    RETR_FAILED,
    RETR_ALL_MESSAGES_OK,
    QUIT_OK,
    QUIT_FAILED
}
