package pop3.client;

public class Message {
    private int id;
    private String to;
    private String from;
    private String cc;
    private String subject;
    private String body;

    public Message(int id) {
        this.to = "";
        this.from = "";
        this.cc = "";
        this.body = "";
        this.subject = "";
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
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

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
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

    public int getId() {
        return id;
    }
}
