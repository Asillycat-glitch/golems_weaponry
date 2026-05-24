package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.world.item.ItemStack;

public interface IProficiencyCounter {
    // 增加点数
    void addPoints(int amount);
    // 获取当前点数
    int getPoints();
    // 尝试升级物品。消耗点数并提升物品的 level
    boolean tryUpgradeItem(ItemStack stack);
}