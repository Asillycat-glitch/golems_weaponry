package a_silly_cat.modulargolems_weaponry.init;

public class ProficiencyCounter {
    private int totalKills = 0;

    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int kills) { this.totalKills = kills; }
    public void incrementKills() { this.totalKills++; }

    // Capability 注册逻辑...
}