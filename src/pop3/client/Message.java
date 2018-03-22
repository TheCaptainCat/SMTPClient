package pop3.client;

import java.util.Stack;

public class Message {
    private Stack<String> to;
    private String from;
    private String subject;
    private String body;

    public Message() {
        this.to = new Stack<>();
        this.from = "";
        this.body = "";
        this.subject = "";
    }

    public String popRecipient() {
        if (!this.to.empty()) {
            return to.pop();
        }
        return null;
    }

    public boolean hasSomeRecipients() {
        return !to.empty();
    }

    public void addRecipient(String to) {
        this.to.push(to);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public String getSubjectWithoutPrefix() {
        return subject.substring(9);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
