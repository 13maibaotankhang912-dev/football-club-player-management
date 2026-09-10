package model;

public class Club extends BaseEntity {

    private String clubName;
    private String sponsorBrand;
    private double budget;

    public Club(String clubId, String clubName, String sponsorBrand, double budget) {
        super(clubId);
        this.clubName     = clubName;
        this.sponsorBrand = sponsorBrand;
        this.budget       = budget;
    }

    public String getClubName()    { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getSponsorBrand()    { return sponsorBrand; }
    public void setSponsorBrand(String sponsorBrand) { this.sponsorBrand = sponsorBrand; }

    public double getBudget()    { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String toFileLine() {
        String budgetStr = (budget == Math.floor(budget))
                ? String.valueOf((long) budget)
                : String.valueOf(budget);
        return getId() + ", " + clubName + ", " + sponsorBrand + ", " + budgetStr;
    }

    @Override
    public String toString() {
        return String.format("Club[%s | %s | %s | %.2f]", getId(), clubName, sponsorBrand, budget);
    }
}
