package pop3.client;

public class Message {
    private String to;
    private String from;
    private String cc;
    private String body;

    public Message() {
        this.to = "";
        this.from = "";
        this.cc = "";
        this.body = "";
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
}
