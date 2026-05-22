// 文件路径: capability/ProficiencyItemCounter.java
package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.nbt.CompoundTag;

public class ProficiencyItemCounter implements IProficiencyItemCounter {
    private int level = 0;
    private String licenseName = "";

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getLicenseName() {
        return licenseName;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ItemLevel", level);
        tag.putString("LicenseName", licenseName);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.level = tag.getInt("ItemLevel");
        this.licenseName = tag.getString("LicenseName");
    }
}