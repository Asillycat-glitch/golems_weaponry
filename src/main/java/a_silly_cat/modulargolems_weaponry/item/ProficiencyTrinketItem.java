// 文件路径: src/main/java/a_silly_cat/modulargolems_weaponry/item/ProficiencyTrinketItem.java
package a_silly_cat.modulargolems_weaponry.item;

import a_silly_cat.modulargolems_weaponry.capability.IProficiencyCounter;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounter;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ProficiencyTrinketItem extends Item {
    public static final String TAG_LEVEL = "ProficiencyLevel";
    private static final int BASE_COST = 100;
    private static final int COST_INCREMENT = 50;
    private final String proficiencyId;

    public ProficiencyTrinketItem(Properties properties, String proficiencyId) {
        super(properties);
        this.proficiencyId = proficiencyId;
    }

    // --- 等级读写工具 ---
    public int getLevel(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(TAG_LEVEL);
    }

    public void setLevel(ItemStack stack, int level) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(TAG_LEVEL, level);
    }

    // --- 升级消耗计算 ---
    public int getUpgradeCost(int currentLevel) {
        return BASE_COST + currentLevel * COST_INCREMENT;
    }

    // --- 交互（潜行右键升级）---
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // ... 这里的升级逻辑保持不变，请直接复制你现有代码中的实现 ...
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                int currentLevel = getLevel(stack);
                // 限制最大等级为 10
                if (currentLevel >= 10) {
                    player.displayClientMessage(Component.literal("已达到最高等级！"), true);
                    return InteractionResultHolder.success(stack);
                }
                LazyOptional<IProficiencyCounter> cap = player.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP);
                cap.ifPresent(counter -> {
                    if (counter.tryUpgradeItem(stack)) {
                        player.displayClientMessage(Component.literal("升级成功！当前等级: " + getLevel(stack)), true);
                    } else {
                        player.displayClientMessage(Component.literal("点数不足！需要: " + getUpgradeCost(currentLevel)), true);
                    }
                });
            }
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, hand);
    }

    // --- Tooltip显示 ---
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        int lv = getLevel(stack);
        tooltip.add(Component.literal("专精: " + proficiencyId));
        tooltip.add(Component.literal("等级: " + lv));
    }

    public String getProficiencyId() {
        return proficiencyId;
    }
}