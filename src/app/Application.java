package app;

import fileio.ClubFileHelper;
import fileio.PlayerFileHelper;
import manager.ClubManager;
import manager.IClubManager;
import manager.IPlayerManager;
import manager.PlayerManager;
import model.Club;
import model.Player;
import utils.ConsoleUtils;
import utils.InputClosedException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Application {

    private static final String CLUBS_FILE   = "clubs.txt";
    private static final String PLAYERS_FILE = "players.txt";

    private final Scanner          scanner;
    private final IClubManager     clubManager;
    private final IPlayerManager   playerManager;
    private final ClubFileHelper   clubFileHelper;
    private final PlayerFileHelper playerFileHelper;
    private final MenuHandler      menuHandler;

    public Application() {
        scanner          = new Scanner(System.in);
        clubManager      = new ClubManager();
        playerManager    = new PlayerManager(clubManager);
        clubFileHelper   = new ClubFileHelper();
        playerFileHelper = new PlayerFileHelper();
        menuHandler      = new MenuHandler(scanner, clubManager, playerManager,
                                           clubFileHelper, playerFileHelper,
                                           CLUBS_FILE, PLAYERS_FILE);
    }

    public void start() {
        loadData(false);

        try {
            int choice;
            do {
                printMenu();
                choice = ConsoleUtils.readInt(scanner, "Enter your choice: ", 1, 14);
                menuHandler.handle(choice);
            } while (choice != 14);
        } catch (InputClosedException ex) {
            // Input stream ended (e.g. redirected/scripted input) without the
            // user explicitly choosing "Quit". Save any unsaved changes
            // before exiting instead of silently discarding them.
            System.out.println("\nInput stream closed. Saving data before exit...");
            menuHandler.saveIfUnsaved();
        } finally {
            scanner.close();
        }
    }

    void loadData(boolean showMessage) {
        try {
            List<Club>   clubs   = clubFileHelper.loadClubs(CLUBS_FILE);
            List<Player> players = playerFileHelper.loadPlayers(PLAYERS_FILE, clubs);
            clubManager.replaceAll(clubs);
            playerManager.replaceAll(players);
            if (showMessage) System.out.println("Load data successfully!");
        } catch (IOException ex) {
            System.out.println("Load data failed!");
        }
    }

    private void printMenu() {
        System.out.println("\n============================================================");
        System.out.println("      FOOTBALL CLUB & PLAYER MANAGEMENT SYSTEM");
        System.out.println("============================================================");
        System.out.println("  1.  List all clubs");
        System.out.println("  2.  Add a new club");
        System.out.println("  3.  Search for a club by ID");
        System.out.println("  4.  Update a club by ID");
        System.out.println("  5.  List clubs with budget <= value");
        System.out.println("  6.  List all players sorted by club name");
        System.out.println("  7.  Search players by partial name");
        System.out.println("  8.  Add a new player");
        System.out.println("  9.  Remove a player by ID");
        System.out.println(" 10.  Update a player by ID");
        System.out.println(" 11.  List players by position");
        System.out.println(" 12.  Save data to files");
        System.out.println(" 13.  Load data from files");
        System.out.println(" 14.  Quit");
        System.out.println("============================================================");
    }
}
