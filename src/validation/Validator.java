package validation;

import model.Position;

public final class Validator {

    public static final String CLUB_ID_REGEX   = "CL-\\d{4}";
    public static final String PLAYER_ID_REGEX = "P\\d{4}";

    private Validator() {}

    public static boolean isClubIdFormat(String value) {
        return value != null && value.trim().matches(CLUB_ID_REGEX);
    }

    public static boolean isPlayerIdFormat(String value) {
        return value != null && value.trim().matches(PLAYER_ID_REGEX);
    }

    public static boolean isPositiveNumber(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try { return Double.parseDouble(value.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean isShirtNumber(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try { int n = Integer.parseInt(value.trim()); return n >= 1 && n <= 99; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean isValidPosition(String value) {
        return Position.from(value) != null;
    }
}
