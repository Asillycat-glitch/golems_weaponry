package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ProficiencyCounterProvider {
    public static final Capability<IProficiencyCounter> PLAYER_COUNTER_CAP = CapabilityManager.get(new CapabilityToken<>(){});
}