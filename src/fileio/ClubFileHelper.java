package fileio;

import model.Club;
import validation.Validator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClubFileHelper {

    public List<Club> loadClubs(String filePath) throws IOException {
        List<Club> clubs = new ArrayList<>();
        if (!Files.exists(Paths.get(filePath))) {
            // No file yet (e.g. very first run) — this is a normal empty
            // state, not an error, so don't throw here.
            return clubs;
        }
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                row++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s*,\\s*", -1);
                if (parts.length != 4)
                    throw new IOException("Club line " + row + ": wrong column count.");

                String id      = parts[0].trim();
                String name    = parts[1].trim();
                String sponsor = parts[2].trim();
                String budgetS = parts[3].trim();

                if (!Validator.isClubIdFormat(id))        throw new IOException("Club line " + row + ": invalid ID.");
                if (containsId(clubs, id))                throw new IOException("Club line " + row + ": duplicate ID.");
                if (name.isEmpty())                       throw new IOException("Club line " + row + ": empty name.");
                if (sponsor.isEmpty())                    throw new IOException("Club line " + row + ": empty sponsor.");
                if (!Validator.isPositiveNumber(budgetS)) throw new IOException("Club line " + row + ": invalid budget.");

                clubs.add(new Club(id, name, sponsor, Double.parseDouble(budgetS)));
            }
        }
        return clubs;
    }

    public boolean saveClubs(String filePath, List<Club> clubs) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            for (Club c : clubs) { bw.write(c.toFileLine()); bw.newLine(); }
            return true;
        } catch (IOException ex) {
            System.out.println("Error writing clubs file: " + ex.getMessage());
            return false;
        }
    }

    private boolean containsId(List<Club> clubs, String id) {
        for (Club c : clubs)
            if (c.getId().equalsIgnoreCase(id)) return true;
        return false;
    }
}
