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

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}
