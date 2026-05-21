package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProficiencyCounterProvider implements ICapabilityProvider {
    public static final Capability<IProficiencyCounter> KILL_COUNTER_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private final ProficiencyCounter instance = new ProficiencyCounter();
    private final LazyOptional<IProficiencyCounter> lazyOptional = LazyOptional.of(() -> instance);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == KILL_COUNTER_CAP ? lazyOptional.cast() : LazyOptional.empty();
    }
}
