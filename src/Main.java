import common.Message;
import common.MessageType;

public class Main {
    public static void main(String[] args) {
        Message msg = new Message(MessageType.LOGIN, "Client-01", "Hello Server", "Watch");
        System.out.println("Tạo thử tin nhắn: " + msg.toString());
        // Nếu in ra được dòng text đẹp đẽ là thành công bước 1!
    }
}