package manager;

import model.Club;

import java.util.List;

public interface IClubManager {
    Club findById(String id);
    boolean existsById(String id);
    List<Club> getAllClubs();
    List<Club> findByMaxBudget(double maxBudget);
    void addClub(Club club);
    boolean existsByName(String name, String excludeClubId);
    void updateClub(Club club, String newName, String newSponsor, String newBudgetText);
    void replaceAll(List<Club> clubs);
    boolean hasUnsavedChanges();
    void clearChangedFlag();
}
