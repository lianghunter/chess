package dataAccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.List;

public interface GameDAO {
    public void clear() throws DataAccessException;
    public void createGame() throws DataAccessException;
    public GameData getGame() throws DataAccessException;
    public List<GameData> listGame() throws DataAccessException;
    public void updateGame() throws DataAccessException;
}
