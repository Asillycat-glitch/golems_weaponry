package a_silly_cat.modulargolems_weaponry.event;

import a_silly_cat.modulargolems_weaponry.modulargolems_weaponry;
import a_silly_cat.modulargolems_weaponry.capability.IProficiencyCounter;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;


@Mod.EventBusSubscriber(modid = modulargolems_weaponry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IProficiencyCounter.class);
    }
}