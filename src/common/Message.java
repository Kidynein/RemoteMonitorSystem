package common;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private String sender;
    private String content;
    private String action;

    // Constructor
    public Message(MessageType type, String sender, String content, String action) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.action = action;
    }
    public Message(MessageType type, String sender, String content) {
        this(type, sender, content, null);
    }

    // Getters và Setters
    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}