package a_silly_cat.modulargolems_weaponry.event;

import a_silly_cat.modulargolems_weaponry.capability.IProficiencyCounter;
import a_silly_cat.modulargolems_weaponry.mgwe;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = mgwe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IProficiencyCounter.class);
    }
}
