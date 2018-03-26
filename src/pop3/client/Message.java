package pop3.client;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public String popRecipient(String domain) {
        String nextRecipient = null;
        if (this.hasSomeRecipients()) {
            for (String s : this.to) {
                if (domain.equals(getDomain(s))) {
                    nextRecipient = s;
                }
            }
        }
        this.to.remove(nextRecipient);
        return nextRecipient;
    }

    public int getNumberOfRecipients() {
        return this.to.size();
    }

    private String getDomain(String address) {
        String[] splitString = address.split("@");
        if (splitString.length > 1) {
            return splitString[1];
        }
        return null;
    }

    public List<String> getAllDomains() {
        List<String> domains = new LinkedList<>();
        for (String s : this.to) {
            String domain = getDomain(s);
            if (domain != null && !domains.contains(domain)) {
                domains.add(domain);
            }
        }
        return domains;
    }

    public List<String> getAllRecipientsOfADomain(String domain) {
        List<String> recipients = new LinkedList<>();
        for (String s : this.to) {
            if (domain.equals(getDomain(s))) {
                recipients.add(s);
            }
        }
        return recipients;
    }

    public boolean hasSomeRecipients() {
        return !to.isEmpty();
    }

    public void addRecipient(String to) {
        this.to.add(to);
    }

    public String getFrom() {
        return from;
    }

    public String getFromDomain() {
        return this.getDomain(this.from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void generateBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("FROM: <");
        sb.append(this.from);
        sb.append(">\nTO: ");
        sb.append(this.to.stream().map(msg -> "<" + msg + ">").collect(Collectors.joining(", ")));
        sb.append("\nSUBJECT: ");
        sb.append(this.subject);
        sb.append("\n\n");
        sb.append(this.body);
        sb.append("\n.");

        this.body = sb.toString();
    }

    public String getBody() {
        return this.body;
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
