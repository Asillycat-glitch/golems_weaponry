package a_silly_cat.modulargolems_weaponry.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerProficiencyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final ProficiencyCounter counter = new ProficiencyCounter();
    private final LazyOptional<IProficiencyCounter> lazyCounter = LazyOptional.of(() -> counter);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ProficiencyCounterProvider.PLAYER_COUNTER_CAP.orEmpty(cap, lazyCounter);
    }

    @Override
    public CompoundTag serializeNBT() {
        return counter.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        counter.deserializeNBT(nbt);
    }
}