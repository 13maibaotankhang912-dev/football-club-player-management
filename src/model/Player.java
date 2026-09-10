package model;

public class Player extends BaseEntity {

    private String   clubId;
    private String   playerName;
    private Position position;
    private int      shirtNumber;

    public Player(String playerId, String clubId, String playerName,
                  Position position, int shirtNumber) {
        super(playerId);
        this.clubId      = clubId;
        this.playerName  = playerName;
        this.position    = position;
        this.shirtNumber = shirtNumber;
    }

    public String   getClubId()      { return clubId; }
    public void     setClubId(String clubId) { this.clubId = clubId; }

    public String   getPlayerName()  { return playerName; }
    public void     setPlayerName(String playerName) { this.playerName = playerName; }

    public Position getPosition()    { return position; }
    public void     setPosition(Position position) { this.position = position; }

    public int      getShirtNumber() { return shirtNumber; }
    public void     setShirtNumber(int shirtNumber) { this.shirtNumber = shirtNumber; }

    public String toFileLine() {
        return getId() + ", " + clubId + ", " + playerName
                + ", " + position.getLabel() + ", " + shirtNumber;
    }

    @Override
    public String toString() {
        return String.format("Player[%s | %s | %s | %s | %d]",
                getId(), clubId, playerName, position.getLabel(), shirtNumber);
    }
}
