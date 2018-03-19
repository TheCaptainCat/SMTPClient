package pop3.client;

import java.util.LinkedList;
import java.util.List;

public class Message {
    private List<String> to;
    private String from;
    private String subject;
    private String body;

    public Message() {
        this.to = new LinkedList<>();
        this.from = "";
        this.body = "";
        this.subject = "";
    }

    public List<String> getTo() {
        return to;
    }

    public void addRecipient(String to) {
        this.to.add(to);
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
