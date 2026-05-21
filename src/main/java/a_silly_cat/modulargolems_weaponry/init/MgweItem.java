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

    static {
        Kill_streak = mgwe.ITEMS.register("kill_streak",
                () -> new SimpleUpgradeItem(new Item.Properties(),
                        () -> MgweModifier.Kill_streak.get(), 1, false));
    }

    public static void register() {
        // 空方法，仅用于触发类加载（执行 static 块）
    }

    @Mod.EventBusSubscriber(modid = mgwe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventHandler {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == GolemItems.UPGRADES.getKey()) {
                event.accept(Kill_streak.get());
            }
        }
    }
}