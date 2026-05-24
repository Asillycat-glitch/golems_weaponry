package a_silly_cat.modulargolems_weaponry.capability;

import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class ProficiencyCounter implements IProficiencyCounter, INBTSerializable<CompoundTag> {
    private int points = 0;

    @Override
    public void addPoints(int amount) {
        this.points += amount;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public boolean tryUpgradeItem(ItemStack stack) {
        if (stack.getItem() instanceof ProficiencyTrinketItem item) {
            int currentLevel = item.getLevel(stack);
            int cost = item.getUpgradeCost(currentLevel);
            if (this.points >= cost) {
                this.points -= cost;
                item.setLevel(stack, currentLevel + 1);
                return true;
            }
        }
        return false;
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
}