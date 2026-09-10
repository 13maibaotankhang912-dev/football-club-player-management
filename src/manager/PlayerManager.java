package manager;

import model.Club;
import model.Player;
import model.Position;
import validation.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlayerManager extends AbstractManager<Player> implements IPlayerManager {

    private final IClubManager clubManager;

    public PlayerManager(IClubManager clubManager) {
        super();
        this.clubManager = clubManager;
    }

    public List<Player> getSortedByClubThenShirt() {
        List<Player> copy = getAll();
        copy.sort(Comparator
                .comparing((Player p) -> clubNameOf(p.getClubId()).toLowerCase(Locale.ROOT))
                .thenComparingInt(Player::getShirtNumber));
        return copy;
    }

    public List<Player> searchByName(String keyword) {
        String lower = (keyword == null) ? "" : keyword.toLowerCase(Locale.ROOT).trim();
        List<Player> result = new ArrayList<>();
        for (Player p : getStore())
            if (p.getPlayerName().toLowerCase(Locale.ROOT).contains(lower)) result.add(p);
        return result;
    }

    public List<Player> filterByPosition(Position position) {
        List<Player> result = new ArrayList<>();
        for (Player p : getStore())
            if (p.getPosition() == position) result.add(p);
        return result;
    }

    public boolean isShirtTaken(String clubId, int shirtNumber, String excludedPlayerId) {
        for (Player p : getStore()) {
            if (!p.getClubId().equalsIgnoreCase(clubId)) continue;
            if (excludedPlayerId != null && p.getId().equalsIgnoreCase(excludedPlayerId)) continue;
            if (p.getShirtNumber() == shirtNumber) return true;
        }
        return false;
    }

    public void addPlayer(Player player) {
        addToStore(player);
    }

    public boolean removeById(String playerId) {
        Player target = findById(playerId);
        return target != null && removeFromStore(target);
    }

    public void updatePlayer(Player player, String newName, String newPositionText, String newShirtText) {
        boolean changed = false;

        if (newName != null && !newName.trim().isEmpty() && !newName.trim().equals(player.getPlayerName())) {
            player.setPlayerName(newName.trim());
            changed = true;
        }
        if (newPositionText != null && !newPositionText.trim().isEmpty()) {
            Position newPos = Position.from(newPositionText);
            if (newPos != null && newPos != player.getPosition()) { player.setPosition(newPos); changed = true; }
        }
        if (newShirtText != null && !newShirtText.trim().isEmpty()) {
            // Defensive guard: only parse if the text is actually a valid
            // shirt number, so a bad value can never throw a
            // NumberFormatException here regardless of what the caller did.
            if (Validator.isShirtNumber(newShirtText.trim())) {
                int num = Integer.parseInt(newShirtText.trim());
                if (num != player.getShirtNumber()) { player.setShirtNumber(num); changed = true; }
            }
        }

        if (changed) markChanged();
    }

    public String clubNameOf(String clubId) {
        Club club = clubManager.findById(clubId);
        return club == null ? "" : club.getClubName();
    }
}
