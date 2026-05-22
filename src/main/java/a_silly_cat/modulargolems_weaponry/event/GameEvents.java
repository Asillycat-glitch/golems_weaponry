// event/GameEvents.java
package a_silly_cat.modulargolems_weaponry.event;

import a_silly_cat.modulargolems_weaponry.mgwe;
import a_silly_cat.modulargolems_weaponry.capability.PlayerProficiencyProvider;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import a_silly_cat.modulargolems_weaponry.command.ProficiencyCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = mgwe.MODID)
public class GameEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    new ResourceLocation(mgwe.MODID, "proficiency_counter"),
                    new PlayerProficiencyProvider()   // 实现了 ICapabilityProvider
            );
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;

        Entity attacker = event.getSource().getEntity();
        if (attacker == null) return;

        Player player = null;
        if (attacker instanceof Player p) {
            player = p;
        } else {
            player = getPlayerOwner(attacker);
        }

        if (player != null) {
            // 改为 PLAYER_COUNTER_CAP 和 addPoints
            player.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP)
                    .ifPresent(cap -> cap.addPoints(1));
        }
    }
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) { // 非死亡克隆（如维度穿越）
            event.getOriginal().reviveCaps(); // 重要：先复活旧玩家的 Capability
            event.getOriginal().getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP).ifPresent(oldCap -> {
                event.getEntity().getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP).ifPresent(newCap -> {
                    // 如果你有一个复制方法，调用它；或者直接通过 NBT 复制
                    // 例如：newCap.deserializeNBT(oldCap.serializeNBT());
                    // 但 ProficiencyCounter 可能没有直接暴露，可以在接口中增加 copyFrom 方法
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ProficiencyCommand.register(event.getDispatcher());
    }

    @Nullable
    private static Player getPlayerOwner(Entity entity) {
        if (entity instanceof OwnableEntity ownable) {
            UUID ownerUUID = ownable.getOwnerUUID();
            if (ownerUUID != null) {
                return entity.level().getPlayerByUUID(ownerUUID);
            }
        }
        try {
            Method method = entity.getClass().getMethod("getOwnerUUID");
            Object result = method.invoke(entity);
            if (result instanceof UUID uuid) {
                return entity.level().getPlayerByUUID(uuid);
            } else if (result instanceof Optional<?> opt && opt.isPresent() && opt.get() instanceof UUID uuid) {
                return entity.level().getPlayerByUUID(uuid);
            }
        } catch (Exception ignored) {}
        return null;
    }
}