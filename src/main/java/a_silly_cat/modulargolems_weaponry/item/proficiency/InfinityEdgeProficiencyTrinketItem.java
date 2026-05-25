package a_silly_cat.modulargolems_weaponry.item.proficiency;

import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class InfinityEdgeProficiencyTrinketItem extends ProficiencyTrinketItem implements ICurioItem {

    public InfinityEdgeProficiencyTrinketItem(Properties properties) {
        super(properties, "infinity_edge");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();

        int level = getLevel(stack);
        if (level == 0) return atts; // 0级无任何属性
        // 攻击力：1级 +35，10级 +70，线性增长
        double attackBonus = 35.0 + (level - 1) * (35.0 / 9.0);
        atts.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(uuid, "infinity_edge_attack", attackBonus, AttributeModifier.Operation.ADDITION));

        // 暴击率：1级 +10%，10级 +25%，线性增长
        double critRateBonus = 0.10 + (level - 1) * (0.15 / 9.0);
        atts.put(L2DamageTracker.CRIT_RATE.get(),
                new AttributeModifier(uuid, "infinity_edge_crit_rate", critRateBonus, AttributeModifier.Operation.ADDITION));

        // 满级被动：预估总暴击率（含自身加成）达到60%时，额外+35%暴击伤害
        if (level == 10) {
            LivingEntity entity = slotContext.entity();
            if (entity != null) {
                double baseCrit = entity.getAttributeValue(L2DamageTracker.CRIT_RATE.get());
                if (baseCrit + critRateBonus >= 0.60) {
                    atts.put(L2DamageTracker.CRIT_DMG.get(),
                            new AttributeModifier(uuid, "infinity_edge_crit_damage", 0.35, AttributeModifier.Operation.ADDITION));
                }
            }
        }

        return atts;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.mgwe.infinity_edge.passive")
                .withStyle(ChatFormatting.GOLD));
    }
}