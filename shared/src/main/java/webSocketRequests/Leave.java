package webSocketRequests;

import webSocketMessages.userCommands.UserGameCommand;

public class Leave extends UserGameCommand{
    public Leave(String authToken, int gameID){
        super(authToken, gameID);
        this.commandType = CommandType.LEAVE;
    }
}
