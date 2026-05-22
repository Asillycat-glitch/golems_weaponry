package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IProficiencyItemCounter extends INBTSerializable<CompoundTag> {
    // 设置专精许可的等级
    void setLevel(int level);
    // 获取专精许可的等级
    int getLevel();
    // 获取专精许可名称（用于显示）
    String getLicenseName();
}