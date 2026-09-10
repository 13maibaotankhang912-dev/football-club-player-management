package model;

public enum Position {
    GOALKEEPER("Goalkeeper"),
    DEFENDER("Defender"),
    MIDFIELDER("Midfielder"),
    FORWARD("Forward"),
    WINGER("Winger");

    private final String label;

    Position(String label) { this.label = label; }

    public String getLabel() { return label; }

    public static Position from(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        for (Position p : values())
            if (p.label.equalsIgnoreCase(text.trim())) return p;
        return null;
    }
}
