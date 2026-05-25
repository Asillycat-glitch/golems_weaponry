// 文件路径: src/main/java/a_silly_cat/modulargolems_weaponry/item/proficiency/CullProficiencyTrinketItem.java
package a_silly_cat.modulargolems_weaponry.item.proficiency;

import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class CullProficiencyTrinketItem extends ProficiencyTrinketItem implements ICurioItem {

    public CullProficiencyTrinketItem(Properties properties) {
        super(properties, "cull");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();
        int level = getLevel(stack);

        // 攻击力
        double attackBonus = 30.0 + level * 2.0;
        atts.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(uuid, "cull_proficiency_attack", attackBonus, AttributeModifier.Operation.ADDITION));

        // 铁魔法：法术冷却缩减（软依赖）
        if (ModList.get().isLoaded("irons_spellbooks")) {
            Attribute cooldownAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("irons_spellbooks", "cooldown_reduction"));
            if (cooldownAttr != null) {
                double cdReduction = 0.2 + level * 0.04;
                atts.put(cooldownAttr,
                        new AttributeModifier(uuid, "cull_proficiency_cooldown", cdReduction, AttributeModifier.Operation.ADDITION));
            }
        }

        // L2DamageTracker：暴击率（软依赖，模组id推测为 l2damagetracker）
        if (ModList.get().isLoaded("l2damagetracker")) {
            Attribute critAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("l2damagetracker", "crit_rate"));
            if (critAttr != null) {
                double critRate = 0.15 + level * 0.01;
                atts.put(critAttr,
                        new AttributeModifier(uuid, "cull_proficiency_crit", critRate, AttributeModifier.Operation.ADDITION));
            }
        }

        return atts;
    }

    /** 判断饰品是否达到满级（10级） */
    public static boolean isMaxLevel(ItemStack stack) {
        if (stack.getItem() instanceof CullProficiencyTrinketItem item) {
            return item.getLevel(stack) >= 10;
        }
        return false;
    }

}