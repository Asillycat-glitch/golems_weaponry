// 文件路径: src/main/java/a_silly_cat/modulargolems_weaponry/init/MgweItem.java
package a_silly_cat.modulargolems_weaponry.init;

import a_silly_cat.modulargolems_weaponry.mgwe;
import dev.xkmc.modulargolems.content.item.upgrade.SimpleUpgradeItem;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class MgweItem {
    public static final RegistryObject<SimpleUpgradeItem> Kill_streak;
    public static final RegistryObject<Item> SWORD_PROFICIENCY;

    static {
        Kill_streak = mgwe.ITEMS.register("kill_streak",
                () -> new SimpleUpgradeItem(new Item.Properties(),
                        () -> MgweModifier.Kill_streak.get(), 1, false));
        SWORD_PROFICIENCY = mgwe.ITEMS.register("sword_proficiency",
                () -> new SimpleUpgradeItem(new Item.Properties(),
                        () -> MgweModifier.PROFICIENCY_TEMPLATE.get(), 1, false)
        );
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
            }
        }
    }
}