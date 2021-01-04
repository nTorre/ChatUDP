package sample.model;

public class Message {

    private String timestamp;
    private boolean sent;

    public Message(String timestamp, boolean sent) {
        this.timestamp = timestamp;
        this.sent = sent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
