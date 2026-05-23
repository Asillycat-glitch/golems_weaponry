package a_silly_cat.modulargolems_weaponry.init;

import a_silly_cat.modulargolems_weaponry.item.CommandStaffItem;
import a_silly_cat.modulargolems_weaponry.mgwe;
import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class MgweItem {
    // Kill_streak 升级保持不变
    public static final RegistryObject<SimpleUpgradeItem> Kill_streak;
    // 剑专精现在是一个通用饰品
    public static final RegistryObject<ProficiencyTrinketItem> SWORD_PROFICIENCY;
    public static final RegistryObject<Item> ARMOR_DISTRIBUTION_UPGRADE;
    public static final RegistryObject<Item> COMMAND_STAFF;
    static {
        Kill_streak = mgwe.ITEMS.register("kill_streak",
                () -> new SimpleUpgradeItem(new Item.Properties(),
                        () -> MgweModifier.Kill_streak.get(), 1, false));
        SWORD_PROFICIENCY = mgwe.ITEMS.register("sword_proficiency",
                () -> new ProficiencyTrinketItem(new Item.Properties().stacksTo(1),
                        "sword", 2.0, 5, 0.25f)
        );
        ARMOR_DISTRIBUTION_UPGRADE = mgwe.ITEMS.register("armor_distribution_upgrade", () -> new SimpleUpgradeItem(new Item.Properties(), () -> MgweModifier.ARMOR_DISTRIBUTION.get(), 1, false));
        COMMAND_STAFF = mgwe.ITEMS.register("command_staff", () -> new CommandStaffItem(new Item.Properties().stacksTo(1)));
    }

    public static void register() {
        // 静态初始化触发
    }

    @Mod.EventBusSubscriber(modid = mgwe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == GolemItems.UPGRADES.getKey()) {
                event.accept(Kill_streak.get());
                event.accept(SWORD_PROFICIENCY.get());
                event.accept(ARMOR_DISTRIBUTION_UPGRADE.get());
                event.accept(COMMAND_STAFF.get());
            }
        }
    }
}