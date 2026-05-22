// 文件路径: capability/ProficiencyCounter.java
package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class ProficiencyCounter implements IProficiencyCounter, INBTSerializable<CompoundTag> {
    // 核心改动：将 currentKills 和 requiredKills 合并为无上限的 points
    private int points = 0;

    // 建议保留 requiredKills 作为升级消耗基数（例如：每升1级消耗100点）
    private final int BASE_COST = 100;

    @Override
    public void addPoints(int amount) {
        this.points += amount;
        // 移除所有升级逻辑
    }

    // 新增方法：消耗 points 来升级物品
    public boolean tryUpgradeItem(IProficiencyItemCounter itemCap) {
        int cost = getUpgradeCost(itemCap.getLevel());
        if (this.points >= cost) {
            this.points -= cost;
            itemCap.setLevel(itemCap.getLevel() + 1);
            return true;
        }
        return false;
    }

    // 新增方法：获取升级所需的点数成本（可根据设计调整公式）
    private int getUpgradeCost(int currentLevel) {
        return BASE_COST + currentLevel * 50;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Points", points);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.points = tag.getInt("Points");
    }

    @Override
    public int getPoints() {
        return points;
    }
}