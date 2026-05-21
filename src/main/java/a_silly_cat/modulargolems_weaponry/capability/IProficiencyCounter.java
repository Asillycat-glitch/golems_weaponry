package a_silly_cat.modulargolems_weaponry.capability;

public interface IProficiencyCounter {
    void addKill(int amount);
    void checkAndUpgrade();

    int getCurrentKills();
    int getRequiredKills();
    int getLevel();
    int getMaxLevel();
}