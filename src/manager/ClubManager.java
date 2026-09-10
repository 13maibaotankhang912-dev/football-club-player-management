package manager;

import model.Club;
import validation.Validator;

import java.util.ArrayList;
import java.util.List;

public class ClubManager extends AbstractManager<Club> implements IClubManager {

    public List<Club> getAllClubs() {
        return getAll();
    }

    public List<Club> findByMaxBudget(double maxBudget) {
        List<Club> result = new ArrayList<>();
        for (Club club : getStore())
            if (club.getBudget() <= maxBudget) result.add(club);
        return result;
    }

    public void addClub(Club club) {
        addToStore(club);
    }

    public boolean existsByName(String name, String excludeClubId) {
        if (name == null) return false;
        String needle = name.trim();
        for (Club c : getStore()) {
            if (excludeClubId != null && c.getId().equalsIgnoreCase(excludeClubId)) continue;
            if (c.getClubName().trim().equalsIgnoreCase(needle)) return true;
        }
        return false;
    }

    public void updateClub(Club club, String newName, String newSponsor, String newBudgetText) {
        boolean changed = false;

        if (newName != null && !newName.trim().isEmpty() && !newName.trim().equals(club.getClubName())) {
            club.setClubName(newName.trim());
            changed = true;
        }
        if (newSponsor != null && !newSponsor.trim().isEmpty() && !newSponsor.trim().equals(club.getSponsorBrand())) {
            club.setSponsorBrand(newSponsor.trim());
            changed = true;
        }
        if (newBudgetText != null && !newBudgetText.trim().isEmpty()) {
            // Defensive guard: only parse if the text is actually a valid
            // positive number, so a bad value can never throw a
            // NumberFormatException here regardless of what the caller did.
            if (Validator.isPositiveNumber(newBudgetText.trim())) {
                double value = Double.parseDouble(newBudgetText.trim());
                if (value != club.getBudget()) { club.setBudget(value); changed = true; }
            }
        }

        if (changed) markChanged();
    }
}
