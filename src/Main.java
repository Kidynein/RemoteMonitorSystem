import common.Message;
import common.MessageType;

public class Main {
    public static void main(String[] args) {
        Message msg = new Message(MessageType.LOGIN, "Client-01", "Hello Server", "Watch");
        System.out.println("Tạo thử tin nhắn: " + msg.toString());
    }
}