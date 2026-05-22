package a_silly_cat.modulargolems_weaponry.item;

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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProficiencyTrinketItem extends Item {

    public static final String TAG_LEVEL = "ProficiencyLevel";
    private static final int BASE_COST = 100;
    private static final int COST_INCREMENT = 50;

    private final String proficiencyId;
    private final double attackPerLevel;
    private final int thresholdLevel;
    private final float bonusDamageMultiplier;

    public ProficiencyTrinketItem(Properties properties, String proficiencyId, double attackPerLevel, int thresholdLevel, float bonusDamageMultiplier) {
        super(properties);
        this.proficiencyId = proficiencyId;
        this.attackPerLevel = attackPerLevel;
        this.thresholdLevel = thresholdLevel;
        this.bonusDamageMultiplier = bonusDamageMultiplier;
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

    // --- 伤害加成计算 ---
    public float getAttackBonus(int level) {
        return (float) (attackPerLevel * level);
    }

    public float getBonusMultiplier(int level) {
        if (level >= thresholdLevel) {
            return bonusDamageMultiplier;
        }
        return 0f;
    }

    // --- 交互（潜行右键升级） ---
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                LazyOptional<a_silly_cat.modulargolems_weaponry.capability.IProficiencyCounter> cap = player.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP);
                cap.ifPresent(counter -> {
                    if (counter.tryUpgradeItem(stack)) {
                        player.displayClientMessage(Component.literal("升级成功！当前等级: " + getLevel(stack)), true);
                    } else {
                        player.displayClientMessage(Component.literal("点数不足！需要: " + getUpgradeCost(getLevel(stack))), true);
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
        tooltip.add(Component.literal("攻击加成: " + getAttackBonus(lv)));
        if (lv >= thresholdLevel) {
            tooltip.add(Component.literal("额外百分比加成: " + (bonusDamageMultiplier * 100) + "%"));
        }
        tooltip.add(Component.literal("下一级消耗: " + getUpgradeCost(lv)));
    }

    public String getProficiencyId() {
        return proficiencyId;
    }
}