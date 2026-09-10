package manager;

import model.Player;
import model.Position;

import java.util.List;

public interface IPlayerManager {
    Player findById(String id);
    boolean existsById(String id);
    List<Player> getAll();
    List<Player> getSortedByClubThenShirt();
    List<Player> searchByName(String keyword);
    List<Player> filterByPosition(Position position);
    boolean isShirtTaken(String clubId, int shirtNumber, String excludedPlayerId);
    void addPlayer(Player player);
    boolean removeById(String playerId);
    void updatePlayer(Player player, String newName, String newPositionText, String newShirtText);
    void replaceAll(List<Player> players);
    boolean hasUnsavedChanges();
    void clearChangedFlag();
}
