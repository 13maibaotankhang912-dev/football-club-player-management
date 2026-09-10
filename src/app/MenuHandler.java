package app;

import fileio.ClubFileHelper;
import fileio.PlayerFileHelper;
import manager.IClubManager;
import manager.IPlayerManager;
import model.Club;
import model.Player;
import model.Position;
import utils.ConsoleUtils;
import utils.TableDisplay;
import validation.Validator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {

    private final Scanner          scanner;
    private final IClubManager     clubManager;
    private final IPlayerManager   playerManager;
    private final ClubFileHelper   clubFileHelper;
    private final PlayerFileHelper playerFileHelper;
    private final String           clubsFile;
    private final String           playersFile;

    public MenuHandler(Scanner scanner,
                       IClubManager clubManager,
                       IPlayerManager playerManager,
                       ClubFileHelper clubFileHelper,
                       PlayerFileHelper playerFileHelper,
                       String clubsFile,
                       String playersFile) {
        this.scanner          = scanner;
        this.clubManager      = clubManager;
        this.playerManager    = playerManager;
        this.clubFileHelper   = clubFileHelper;
        this.playerFileHelper = playerFileHelper;
        this.clubsFile        = clubsFile;
        this.playersFile      = playersFile;
    }

    public void handle(int choice) {
        switch (choice) {
            case 1:  listAllClubs();          break;
            case 2:  addClub();               break;
            case 3:  searchClubById();        break;
            case 4:  updateClub();            break;
            case 5:  listClubsByBudget();     break;
            case 6:  listPlayersSorted();     break;
            case 7:  searchPlayersByName();   break;
            case 8:  addPlayer();             break;
            case 9:  removePlayer();          break;
            case 10: updatePlayer();          break;
            case 11: listPlayersByPosition(); break;
            case 12: saveData();              break;
            case 13: reloadData();            break;
            case 14: quit();                  break;
            default: System.out.println("Invalid option."); break;
        }
    }

    private void listAllClubs() {
        TableDisplay.showClubs(clubManager.getAllClubs());
    }

    private void addClub() {
        String id      = promptNewClubId();
        String name    = promptNewClubName();
        String sponsor = ConsoleUtils.readNonBlank(scanner, "Sponsor brand   : ");
        double budget  = promptPositiveBudget("Budget (million): ");
        clubManager.addClub(new Club(id, name, sponsor, budget));
        System.out.println("Club added successfully!");
    }

    private void searchClubById() {
        String id   = ConsoleUtils.readNonBlank(scanner, "Enter Club ID: ");
        Club   club = clubManager.findById(id);
        if (club == null) {
            System.out.println("This club does not exist!");
        } else {
            TableDisplay.showClubs(Collections.singletonList(club));
        }
    }

    private void updateClub() {
        String id   = ConsoleUtils.readNonBlank(scanner, "Enter Club ID: ");
        Club   club = clubManager.findById(id);
        if (club == null) { System.out.println("This club does not exist!"); return; }

        String name    = promptOptionalClubName(club);
        String sponsor = ConsoleUtils.readLine(scanner, "New sponsor     (blank = skip): ");
        String budgetS = promptOptionalBudget("New budget      (blank = skip): ");
        clubManager.updateClub(club, name, sponsor, budgetS);
        System.out.println("Club updated successfully!");
    }

    private void listClubsByBudget() {
        double max = promptPositiveBudget("Max budget (million): ");
        TableDisplay.showClubs(clubManager.findByMaxBudget(max));
    }

    private void listPlayersSorted() {
        TableDisplay.showPlayers(playerManager.getSortedByClubThenShirt(), clubManager);
    }

    private void searchPlayersByName() {
        String keyword = ConsoleUtils.readNonBlank(scanner, "Enter partial name: ");
        TableDisplay.showPlayers(playerManager.searchByName(keyword), clubManager);
    }

    private void addPlayer() {
        if (clubManager.getAllClubs().isEmpty()) {
            System.out.println("No clubs available. Please add a club first!");
            return;
        }
        TableDisplay.showClubs(clubManager.getAllClubs());
        String   pid   = promptNewPlayerId();
        String   cid   = promptExistingClubId();
        String   name  = ConsoleUtils.readNonBlank(scanner, "Player name    : ");
        Position pos   = promptPosition("Position       : ");
        int      shirt = promptFreshShirtNumber(cid, null);
        playerManager.addPlayer(new Player(pid, cid, name, pos, shirt));
        System.out.println("Player added successfully!");
    }

    private void removePlayer() {
        String  id      = ConsoleUtils.readNonBlank(scanner, "Enter Player ID: ");
        boolean removed = playerManager.removeById(id);
        System.out.println(removed ? "Player removed successfully!" : "This player does not exist!");
    }

    private void updatePlayer() {
        String id     = ConsoleUtils.readNonBlank(scanner, "Enter Player ID: ");
        Player player = playerManager.findById(id);
        if (player == null) { System.out.println("This player does not exist!"); return; }

        String name    = ConsoleUtils.readLine(scanner, "New player name (blank = skip): ");
        String posText = promptOptionalPosition("New position    (blank = skip): ");
        String shirtS  = promptOptionalShirtNumber(player.getClubId(), id,
                                                   "New shirt no.   (blank = skip): ");
        playerManager.updatePlayer(player, name, posText, shirtS);
        System.out.println("Player updated successfully!");
    }

    private void listPlayersByPosition() {
        Position pos = promptPosition("Enter position: ");
        TableDisplay.showPlayers(playerManager.filterByPosition(pos), clubManager);
    }

    private void saveData() {
        List<Club>   clubs   = clubManager.getAllClubs();
        List<Player> players = playerManager.getAll();
        boolean ok = clubFileHelper.saveClubs(clubsFile, clubs)
                  && playerFileHelper.savePlayers(playersFile, players);
        if (ok) {
            clubManager.clearChangedFlag();
            playerManager.clearChangedFlag();
            System.out.println("Save data successfully!");
        } else {
            System.out.println("Save data failed!");
        }
    }

    private void reloadData() {
        try {
            List<Club>   clubs   = clubFileHelper.loadClubs(clubsFile);
            List<Player> players = playerFileHelper.loadPlayers(playersFile, clubs);
            clubManager.replaceAll(clubs);
            playerManager.replaceAll(players);
            System.out.println("Load data successfully!");
        } catch (IOException ex) {
            System.out.println("Load data failed!");
        }
    }

    private void quit() {
        if (hasUnsavedChanges()) {
            System.out.println("Unsaved changes detected. Saving before exit...");
            saveData();
        }
        System.out.println("Goodbye!");
    }

    /** Public entry point used by Application when the input stream ends
     *  unexpectedly (e.g. scripted/redirected input), so unsaved changes are
     *  never silently lost. */
    public void saveIfUnsaved() {
        if (hasUnsavedChanges()) {
            saveData();
        }
    }

    private boolean hasUnsavedChanges() {
        return clubManager.hasUnsavedChanges() || playerManager.hasUnsavedChanges();
    }

    // ── Input helpers ─────────────────────────────────────────────────────────

    private String promptNewClubId() {
        while (true) {
            String id = ConsoleUtils.readNonBlank(scanner, "Club ID (CL-xxxx): ").toUpperCase();
            if (!Validator.isClubIdFormat(id))      System.out.println("Invalid format! Club ID must be CL-xxxx (e.g. CL-0001).");
            else if (clubManager.existsById(id))    System.out.println("This club ID already exists!");
            else return id;
        }
    }

    private String promptNewClubName() {
        while (true) {
            String name = ConsoleUtils.readNonBlank(scanner, "Club name       : ");
            if (clubManager.existsByName(name, null)) System.out.println("A club with this name already exists!");
            else return name;
        }
    }

    private String promptOptionalClubName(Club club) {
        while (true) {
            String name = ConsoleUtils.readLine(scanner, "New club name   (blank = skip): ");
            if (name.isEmpty()) return name;
            if (clubManager.existsByName(name, club.getId())) System.out.println("A club with this name already exists!");
            else return name;
        }
    }

    private String promptNewPlayerId() {
        while (true) {
            String id = ConsoleUtils.readNonBlank(scanner, "Player ID (Pxxxx): ").toUpperCase();
            if (!Validator.isPlayerIdFormat(id))    System.out.println("Invalid format! Player ID must be Pxxxx (e.g. P0001).");
            else if (playerManager.existsById(id))  System.out.println("This player ID already exists!");
            else return id;
        }
    }

    private String promptExistingClubId() {
        while (true) {
            String id = ConsoleUtils.readNonBlank(scanner, "Club ID: ").toUpperCase();
            if (clubManager.findById(id) != null) return id;
            System.out.println("This club does not exist!");
        }
    }

    private double promptPositiveBudget(String prompt) {
        while (true) {
            String s = ConsoleUtils.readNonBlank(scanner, prompt);
            if (Validator.isPositiveNumber(s)) return Double.parseDouble(s);
            System.out.println("Budget must be a positive number!");
        }
    }

    private String promptOptionalBudget(String prompt) {
        while (true) {
            String s = ConsoleUtils.readLine(scanner, prompt);
            if (s.isEmpty() || Validator.isPositiveNumber(s)) return s;
            System.out.println("Budget must be a positive number!");
        }
    }

    private Position promptPosition(String prompt) {
        while (true) {
            String   s = ConsoleUtils.readNonBlank(scanner, prompt);
            Position p = Position.from(s);
            if (p != null) return p;
            System.out.println("Position must be: Goalkeeper, Defender, Midfielder, Forward, or Winger.");
        }
    }

    private String promptOptionalPosition(String prompt) {
        while (true) {
            String s = ConsoleUtils.readLine(scanner, prompt);
            if (s.isEmpty() || Validator.isValidPosition(s)) return s;
            System.out.println("Position must be: Goalkeeper, Defender, Midfielder, Forward, or Winger.");
        }
    }

    private int promptFreshShirtNumber(String clubId, String excludedPlayerId) {
        while (true) {
            String s = ConsoleUtils.readNonBlank(scanner, "Shirt number (1-99): ");
            if (!Validator.isShirtNumber(s)) { System.out.println("Shirt number must be an integer between 1 and 99!"); continue; }
            int num = Integer.parseInt(s);
            if (playerManager.isShirtTaken(clubId, num, excludedPlayerId)) System.out.println("This shirt number already exists in this club!");
            else return num;
        }
    }

    private String promptOptionalShirtNumber(String clubId, String excludedPlayerId, String prompt) {
        while (true) {
            String s = ConsoleUtils.readLine(scanner, prompt);
            if (s.isEmpty()) return s;
            if (!Validator.isShirtNumber(s)) { System.out.println("Shirt number must be an integer between 1 and 99!"); continue; }
            int num = Integer.parseInt(s);
            if (playerManager.isShirtTaken(clubId, num, excludedPlayerId)) System.out.println("This shirt number already exists in this club!");
            else return s;
        }
    }
}
