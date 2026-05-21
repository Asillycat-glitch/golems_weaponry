package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class ProficiencyCounter implements IProficiencyCounter, INBTSerializable<CompoundTag> {
    private int currentKills = 0;
    private int requiredKills = 100;   // 可通过配置文件调整
    private int currentLevel = 0;
    private int maxLevel = 10;          // 可通过配置文件调整

    @Override
    public void addKill(int amount) {
        this.currentKills += amount;
        if (this.currentKills >= this.requiredKills) {
            checkAndUpgrade();
        }
    }

    @Override
    public void checkAndUpgrade() {
        while (currentKills >= requiredKills && currentLevel < maxLevel) {
            currentKills -= requiredKills;
            currentLevel++;
            requiredKills = 100 + currentLevel * 50;   // 公式可配置
        }
        // 限制击杀数不超过需求太多，防止溢出
        if (currentKills > requiredKills) currentKills = requiredKills;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Kills", currentKills);
        tag.putInt("Required", requiredKills);
        tag.putInt("Level", currentLevel);
        tag.putInt("MaxLevel", maxLevel);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.currentKills = tag.getInt("Kills");
        this.requiredKills = tag.getInt("Required");
        this.currentLevel = tag.getInt("Level");
        this.maxLevel = tag.getInt("MaxLevel");
    }
    @Override
    public int getCurrentKills() { return currentKills; }

    @Override
    public int getRequiredKills() { return requiredKills; }

    @Override
    public int getLevel() { return currentLevel; }

    @Override
    public int getMaxLevel() { return maxLevel; }

}