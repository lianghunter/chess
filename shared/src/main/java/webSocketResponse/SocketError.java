package webSocketResponse;
import webSocketMessages.serverMessages.ServerMessage;
public class SocketError extends ServerMessage{
    private final String errorMessage;
    public SocketError(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
