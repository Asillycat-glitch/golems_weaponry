package a_silly_cat.modulargolems_weaponry.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import java.util.UUID;

public class BowProficiencyTrinketItem extends ProficiencyTrinketItem implements ICurioItem {
    // 正确的弹射物伤害属性 ID，来自 L2DamageTracker
    private static final ResourceLocation BOW_STRENGTH_ID = new ResourceLocation("l2damagetracker", "bow_strength");

    private Attribute getBowStrengthAttribute() {
        return ForgeRegistries.ATTRIBUTES.getValue(BOW_STRENGTH_ID);
    }

    public BowProficiencyTrinketItem(Properties properties) {
        super(properties, "bow");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> atts = LinkedHashMultimap.create();
        Attribute attr = getBowStrengthAttribute();
        if (attr != null) {
            int level = getLevel(stack);
            double bonus = level * 0.3;   // 每级+30%弹射物伤害
            atts.put(attr, new AttributeModifier(uuid, "bow_proficiency_bow_strength", bonus, AttributeModifier.Operation.ADDITION));
        }
        return atts;
    }
}