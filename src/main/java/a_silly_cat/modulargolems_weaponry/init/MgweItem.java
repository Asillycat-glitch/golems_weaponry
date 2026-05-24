// 文件: src/main/java/a_silly_cat/modulargolems_weaponry/init/MgweItem.java
package a_silly_cat.modulargolems_weaponry.init;

import a_silly_cat.modulargolems_weaponry.mgwe;
import a_silly_cat.modulargolems_weaponry.item.SwordProficiencyTrinketItem;
import a_silly_cat.modulargolems_weaponry.item.AxeProficiencyTrinketItem;
import a_silly_cat.modulargolems_weaponry.item.BowProficiencyTrinketItem;
import a_silly_cat.modulargolems_weaponry.item.CommandStaffItem;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class MgweItem {
    public static final RegistryObject<Item> Kill_streak;
    public static final RegistryObject<Item> SWORD_PROFICIENCY;
    public static final RegistryObject<Item> AXE_PROFICIENCY;
    public static final RegistryObject<Item> BOW_PROFICIENCY;
    public static final RegistryObject<Item> ARMOR_DISTRIBUTION_UPGRADE;
    public static final RegistryObject<Item> COMMAND_STAFF;

    static {
        Kill_streak = mgwe.ITEMS.register("kill_streak", () ->
                new SimpleUpgradeItem(new Item.Properties(), () -> MgweModifier.Kill_streak.get(), 1, false));

        SWORD_PROFICIENCY = mgwe.ITEMS.register("sword_proficiency", () ->
                new SwordProficiencyTrinketItem(new Item.Properties().stacksTo(1)));

        AXE_PROFICIENCY = mgwe.ITEMS.register("axe_proficiency", () ->
                new AxeProficiencyTrinketItem(new Item.Properties().stacksTo(1)));

        BOW_PROFICIENCY = mgwe.ITEMS.register("bow_proficiency", () ->
                new BowProficiencyTrinketItem(new Item.Properties().stacksTo(1)));

        ARMOR_DISTRIBUTION_UPGRADE = mgwe.ITEMS.register("armor_distribution_upgrade", () ->
                new SimpleUpgradeItem(new Item.Properties(), () -> MgweModifier.ARMOR_DISTRIBUTION.get(), 1, false));

        COMMAND_STAFF = mgwe.ITEMS.register("command_staff", () ->
                new CommandStaffItem(new Item.Properties().stacksTo(1)));
    }

    public static void register() { /* 静态初始化触发 */ }
}