// 文件路径: capability/ProficiencyCounterProvider.java
package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ProficiencyCounterProvider {
    // 保留玩家端能力（用于存储 points）
    public static final Capability<IProficiencyCounter> PLAYER_COUNTER_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    // 新增物品端能力（用于存储专精许可的 levels）
    public static final Capability<IProficiencyItemCounter> ITEM_COUNTER_CAP = CapabilityManager.get(new CapabilityToken<>() {});
}