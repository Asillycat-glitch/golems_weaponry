// 文件: AxeProficiencyTrinketItem.java
package a_silly_cat.modulargolems_weaponry.item.proficiency;

import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class AxeProficiencyTrinketItem extends ProficiencyTrinketItem implements ICurioItem {
    public AxeProficiencyTrinketItem(Properties properties) {
        super(properties, "axe");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();
        int level = getLevel(stack);
        double bonus = level * 4.0;   // 每级+4生命上限
        atts.put(Attributes.MAX_HEALTH,
                new AttributeModifier(uuid, "axe_proficiency_health", bonus, AttributeModifier.Operation.ADDITION));
        return atts;
    }
}