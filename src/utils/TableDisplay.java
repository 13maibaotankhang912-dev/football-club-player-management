package utils;

import manager.IClubManager;
import model.Club;
import model.Player;

import java.util.List;

public final class TableDisplay {

    private static final int W_CLUB_ID    = 10;
    private static final int W_CLUB_NAME  = 30;
    private static final int W_SPONSOR    = 20;
    private static final int W_BUDGET     = 10;
    private static final int W_PLAYER_ID  = 8;
    private static final int W_PLAYER_NAME = 26;
    private static final int W_POSITION   = 12;
    private static final int W_SHIRT      = 4;

    private TableDisplay() {}

    // Accepts a plain List<Club> — no Manager dependency
    public static void showClubs(List<Club> clubs) {
        if (clubs.isEmpty()) { System.out.println("  (No clubs to display.)"); return; }

        String line = row('-', W_CLUB_ID, W_CLUB_NAME, W_SPONSOR, W_BUDGET);
        System.out.println(line);
        System.out.printf("| %-" + W_CLUB_ID + "s | %-" + W_CLUB_NAME + "s | %-"
                        + W_SPONSOR + "s | %" + W_BUDGET + "s |%n",
                "Club ID", "Club Name", "Sponsor Brand", "Budget");
        System.out.println(line);

        for (Club c : clubs)
            System.out.printf("| %-" + W_CLUB_ID + "s | %-" + W_CLUB_NAME + "s | %-"
                            + W_SPONSOR + "s | %" + W_BUDGET + ".2f |%n",
                    c.getId(), c.getClubName(), c.getSponsorBrand(), c.getBudget());

        System.out.println(line);
        System.out.printf("  Total: %d club(s)%n", clubs.size());
    }

    // ClubManager is still needed to resolve club name for each player row.
    // Ideally callers pass List<Player> with pre-resolved names, but that would
    // require a new DTO — keeping ClubManager here is an acceptable trade-off
    // for a LAB-scale project.
    public static void showPlayers(List<Player> players, IClubManager clubManager) {
        if (players.isEmpty()) { System.out.println("  (No players to display.)"); return; }

        String line = row('-', W_PLAYER_ID, W_CLUB_ID, W_CLUB_NAME, W_PLAYER_NAME, W_POSITION, W_SHIRT);
        System.out.println(line);
        System.out.printf("| %-" + W_PLAYER_ID + "s | %-" + W_CLUB_ID + "s | %-"
                        + W_CLUB_NAME + "s | %-" + W_PLAYER_NAME + "s | %-"
                        + W_POSITION + "s | %-" + W_SHIRT + "s |%n",
                "PID", "Club ID", "Club Name", "Player Name", "Position", "No.");
        System.out.println(line);

        for (Player p : players) {
            Club   club     = clubManager == null ? null : clubManager.findById(p.getClubId());
            String clubName = club == null ? "" : club.getClubName();
            System.out.printf("| %-" + W_PLAYER_ID + "s | %-" + W_CLUB_ID + "s | %-"
                            + W_CLUB_NAME + "s | %-" + W_PLAYER_NAME + "s | %-"
                            + W_POSITION + "s | %-" + W_SHIRT + "d |%n",
                    p.getId(), p.getClubId(), clubName,
                    p.getPlayerName(), p.getPosition().getLabel(), p.getShirtNumber());
        }

        System.out.println(line);
        System.out.printf("  Total: %d player(s)%n", players.size());
    }

    private static String row(char fill, int... widths) {
        StringBuilder sb = new StringBuilder();
        for (int w : widths) {
            sb.append('+');
            for (int i = 0; i < w + 2; i++) sb.append(fill);
        }
        sb.append('+');
        return sb.toString();
    }
}
