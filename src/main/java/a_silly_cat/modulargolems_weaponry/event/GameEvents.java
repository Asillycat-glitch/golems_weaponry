package a_silly_cat.modulargolems_weaponry.event;

import a_silly_cat.modulargolems_weaponry.mgwe;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import a_silly_cat.modulargolems_weaponry.command.ProficiencyCommand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
                    new ProficiencyCounterProvider()
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
            player.getCapability(ProficiencyCounterProvider.KILL_COUNTER_CAP).ifPresent(cap -> cap.addKill(1));
        }
    }

    // 注册指令
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