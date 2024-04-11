package webSocketResponse;
import webSocketMessages.serverMessages.ServerMessage;
public class Notification extends ServerMessage{
    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }


    private String message;

}
