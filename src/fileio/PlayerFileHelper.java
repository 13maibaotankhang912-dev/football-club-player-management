package fileio;

import model.Club;
import model.Player;
import model.Position;
import validation.Validator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlayerFileHelper {

    public List<Player> loadPlayers(String filePath, List<Club> clubs) throws IOException {
        List<Player> players = new ArrayList<>();
        if (!Files.exists(Paths.get(filePath))) {
            // No file yet (e.g. very first run) — this is a normal empty
            // state, not an error, so don't throw here.
            return players;
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                row++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s*,\\s*", -1);
                if (parts.length != 5)
                    throw new IOException("Player line " + row + ": wrong column count.");

                String pid     = parts[0].trim();
                String clubId  = parts[1].trim();
                String name    = parts[2].trim();
                String posText = parts[3].trim();
                String shirtS  = parts[4].trim();

                if (!Validator.isPlayerIdFormat(pid))     throw new IOException("Player line " + row + ": invalid ID.");
                if (containsId(players, pid))             throw new IOException("Player line " + row + ": duplicate ID.");
                if (!clubExists(clubs, clubId))           throw new IOException("Player line " + row + ": unknown club.");
                if (name.isEmpty())                       throw new IOException("Player line " + row + ": empty name.");

                Position pos = Position.from(posText);
                if (pos == null)                          throw new IOException("Player line " + row + ": invalid position.");
                if (!Validator.isShirtNumber(shirtS))     throw new IOException("Player line " + row + ": invalid shirt.");

                int shirt = Integer.parseInt(shirtS);
                if (shirtTaken(players, clubId, shirt))   throw new IOException("Player line " + row + ": duplicate shirt.");

                players.add(new Player(pid, clubId, name, pos, shirt));
            }
        }
        return players;
    }

    public boolean savePlayers(String filePath, List<Player> players) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            for (Player p : players) { bw.write(p.toFileLine()); bw.newLine(); }
            return true;
        } catch (IOException ex) {
            System.out.println("Error writing players file: " + ex.getMessage());
            return false;
        }
    }

    private boolean containsId(List<Player> players, String id) {
        for (Player p : players)
            if (p.getId().equalsIgnoreCase(id)) return true;
        return false;
    }

    private boolean clubExists(List<Club> clubs, String clubId) {
        for (Club c : clubs)
            if (c.getId().equalsIgnoreCase(clubId)) return true;
        return false;
    }

    private boolean shirtTaken(List<Player> players, String clubId, int shirt) {
        for (Player p : players)
            if (p.getClubId().equalsIgnoreCase(clubId) && p.getShirtNumber() == shirt) return true;
        return false;
    }
}
