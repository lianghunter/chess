package webSocketRequests;

import webSocketMessages.userCommands.UserGameCommand;
public class ObserverJoin extends UserGameCommand{
    public ObserverJoin(String authToken, int gameID){
        super(authToken, gameID);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
