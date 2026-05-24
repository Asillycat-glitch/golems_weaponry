package a_silly_cat.modulargolems_weaponry.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class SwordProficiencyTrinketItem extends ProficiencyTrinketItem implements ICurioItem {

    public SwordProficiencyTrinketItem(Properties properties) {
        super(properties, "sword");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();

        int level = getLevel(stack);
        double bonus = level * 2.0; // 每级+2攻击力

        atts.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "sword_proficiency_damage", bonus, AttributeModifier.Operation.ADDITION));

        return atts;
    }
}